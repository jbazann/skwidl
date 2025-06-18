package dev.jbazann.skwidl.customers.site;

import dev.jbazann.skwidl.customers.site.dto.NewSiteDTO;
import dev.jbazann.skwidl.customers.site.dto.SiteDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/sites")
public class SiteController {

    private final SiteService siteService;

    public SiteController(final SiteService siteService) {
        this.siteService = siteService;
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<SiteDTO> getSite(@RequestParam("id") final UUID id,
                                                 @RequestParam("status") final Site.SiteStatus status,
                                                 @RequestParam("customer_id") final UUID customerId)
    {
        if (id == null && customerId == null && status == null) {
            throw new IllegalArgumentException("Must provide at least one of: id, status, customer_id.");
        }
        return siteService.findSitesByExample(
                new Site().setId(id).setStatus(status).setCustomer(customerId)
        ).stream().map(Site::toDto).toList();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public SiteDTO createSite(@RequestBody final NewSiteDTO site) {
        return siteService.newSite(site).toDto();
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updateSiteStatus(@PathVariable("id") final UUID id,
                                                    @RequestBody final Site.SiteStatus status) {
        siteService.updateSiteStatus(id, status);
    }

}
