package ru.clevertec.ecl.web.controller;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.clevertec.ecl.service.GiftCertificateService;
import ru.clevertec.ecl.service.dto.GiftCertificateDto;
import ru.clevertec.ecl.service.dto.QueryParamsDto;
import ru.clevertec.ecl.service.exception.ClientException;

/**
 * Rest Controller for  creating, updating, deleting and getting certificates by parameters.
 * <p>
 * General contract for the verb GET:
 * url/api/certificates?cert=name:like:skydiv,descr:like:airpl&tag=name:like:extr&order=date:desc&page=18&size=11
 * <p>
 * General contract for the verbs POST, PUT:
 * url/api/certificates?cert=name:massage,descr:back massage lasting 1 hour,price:123.45,duration:12&tag=name:health,name:beauty
 */
@RestController
@RequestMapping("api/certificates")
@RequiredArgsConstructor
public class RestCertificateController {

    private final GiftCertificateService giftCertificateService;

    @ModelAttribute
    public QueryParamsDto paramsDto() {
        return new QueryParamsDto();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        giftCertificateService.delete(id);
    }

    /**
     * endpoint for certificate generation by request parameters.
     * Mapping examples:
     * for 1 tag:
     * url/api/certificates?cert=name:massage,descr:back massage lasting 1 hour,price:123.45,duration:12&tag=name:health
     * for 2 tags:
     * url/api/certificates?cert=name:massage,descr:back massage lasting 1 hour,price:123.45,duration:12&tag=name:health,name:beauty
     */
    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<GiftCertificateDto> create(@ModelAttribute QueryParamsDto paramsDto) {
        GiftCertificateDto created = giftCertificateService.createByParams(paramsDto);
        return buildResponseCreated(created);
    }

    /**
     * endpoint to renew an existing certificate by request parameters
     * Mapping examples:
     * for 1 tag:
     * url/api/certificates/1?cert=name:massage,descr:back massage lasting 1 hour,price:123.45,duration:12&tag=name:health
     * for 2 tags:
     * url/api/certificates/1?cert=name:massage,descr:back massage lasting 1 hour,price:123.45,duration:12&tag=name:health,name:beauty
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GiftCertificateDto update(@ModelAttribute QueryParamsDto paramsDto, @PathVariable Long id) {
        return giftCertificateService.updateByParams(paramsDto, id);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<GiftCertificateDto> findByParams(@ModelAttribute("paramsDto") QueryParamsDto paramsDto) {
        return giftCertificateService.findByParams(paramsDto);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public Page<GiftCertificateDto> findAll(Pageable pageable) {
        return giftCertificateService.findAll(pageable);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GiftCertificateDto getById(@PathVariable Long id) {
        return giftCertificateService.findById(id);
    }

    /**
     * endpoint to update the price of an existing certificate
     * Mapping example:
     *  url/api/certificates/1?price:123.45
     */
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public GiftCertificateDto updatePrice(@PathVariable Long id, @RequestParam(value = "price") String priceStr) {
        GiftCertificateDto dto = giftCertificateService.findById(id);
        BigDecimal price;
        try {
            price = new BigDecimal(priceStr);
        } catch (NumberFormatException e) {
            throw new ClientException("invalid price value", e, "40031");
        }
        if (price.scale() > 2) {
            throw new ClientException("scale cannot be more than two", "40031");
        }
        dto.setPrice(price);
        return giftCertificateService.update(dto);
    }

    private ResponseEntity<GiftCertificateDto> buildResponseCreated(GiftCertificateDto created) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(getLocation(created))
                .body(created);
    }

    private URI getLocation(GiftCertificateDto created) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/certificates/{id}")
                .buildAndExpand(created.getId())
                .toUri();
    }
}
