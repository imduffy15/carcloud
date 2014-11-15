'use strict';

/* App Module */
var httpHeaders;

var carcloudApp = angular.module('carcloudApp', ['http-auth-interceptor',
    'ngResource', 'ngRoute', 'ngCookies', 'carcloudAppUtils', 'truncate']);

carcloudApp
    .config(function ($routeProvider, $httpProvider, $sceDelegateProvider, USER_ROLES) {
        $routeProvider
            .when('/register', {
                templateUrl: 'views/register.html',
                controller: 'RegisterController',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            })
            .when('/activate', {
                templateUrl: 'views/activate.html',
                controller: 'ActivationController',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            })
            .when('/login', {
                templateUrl: 'views/login.html',
                controller: 'LoginController',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            })
            .when('/error', {
                templateUrl: 'views/error.html',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            })
            .when('/settings', {
                templateUrl: 'views/settings.html',
                controller: 'SettingsController',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            })
            .when('/password', {
                templateUrl: 'views/password.html',
                controller: 'PasswordController',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            })
            .when('/sessions', {
                templateUrl: 'views/sessions.html',
                controller: 'SessionsController',
                resolve: {
                    resolvedSessions: ['Sessions', function (Sessions) {
                        return Sessions.get();
                    }]
                },
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            })
            .when('/metrics', {
                templateUrl: 'views/metrics.html',
                controller: 'MetricsController',
                access: {
                    authorizedRoles: [USER_ROLES.admin]
                }
            })
            .when('/logs', {
                templateUrl: 'views/logs.html',
                controller: 'LogsController',
                resolve: {
                    resolvedLogs: ['LogsService', function (LogsService) {
                        return LogsService.findAll();
                    }]
                },
                access: {
                    authorizedRoles: [USER_ROLES.admin]
                }
            })
            .when('/audits', {
                templateUrl: 'views/audits.html',
                controller: 'AuditsController',
                access: {
                    authorizedRoles: [USER_ROLES.admin]
                }
            })
            .when('/logout', {
                templateUrl: 'views/main.html',
                controller: 'LogoutController',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            })
            .when('/docs', {
                templateUrl: 'views/docs.html',
                access: {
                    authorizedRoles: [USER_ROLES.admin]
                }
            })
            .otherwise({
                templateUrl: 'views/main.html',
                controller: 'MainController',
                access: {
                    authorizedRoles: [USER_ROLES.all]
                }
            });

        httpHeaders = $httpProvider.defaults.headers;

    })
    .run(function ($rootScope, $location, $http, AuthenticationSharedService, Session, USER_ROLES) {
        // SPAM 0.0.1 - Example of event listeners
        $rootScope.$on('$routeChangeStart', function (event, next) {
            $rootScope.isAuthorized = AuthenticationSharedService.isAuthorized;
            $rootScope.userRoles = USER_ROLES;
            AuthenticationSharedService.valid(next.access.authorizedRoles);
        });

        // Call when the the client is confirmed
        // SPAM 0.0.1 - Example of event listeners
        $rootScope.$on('event:auth-loginConfirmed', function (data) {
            $rootScope.authenticated = true;
            if ($location.path() === "/login") {
                $location.path('/').replace();
            }
        });

        // Call when the 401 response is returned by the server
        // SPAM 0.0.1 - Example of event listeners
        $rootScope.$on('event:auth-loginRequired', function (rejection) {
            AuthenticationSharedService.refresh();
        });

        // Call when the 403 response is returned by the server
        // SPAM 0.0.1 - Example of event listeners
        $rootScope.$on('event:auth-notAuthorized', function (rejection) {
            $rootScope.errorMessage = 'errors.403';
            $location.path('/error').replace();
        });

        // Call when the user logs out
        // SPAM 0.0.1 - Example of event listeners
        $rootScope.$on('event:auth-loginCancelled', function () {
            $location.path('');
        });
    });
