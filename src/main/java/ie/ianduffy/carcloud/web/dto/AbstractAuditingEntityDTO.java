package ie.ianduffy.carcloud.web.dto;

import lombok.Data;
import org.springframework.hateoas.Link;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;

@Data
public abstract class AbstractAuditingEntityDTO {

    private final Map<String, String> links;

    protected int version;

    AbstractAuditingEntityDTO() {
        this.links = new HashMap<>();
    }

    public void add(Link link) {
        this.links.put(link.getRel(), link.getHref());
    }

    public void add(Iterable<Link> links) {
        Assert.notNull(links, "Given links must not be null!");
        for (Link candidate : links) {
            add(candidate);
        }
    }

    public Link getLink(String rel) {
        Link link = null;
        if (links.get(rel) != null) {
            link = new Link(links.get(rel), rel);
        }
        return link;
    }
}