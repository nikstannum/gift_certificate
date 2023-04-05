package ru.clevertec.ecl.web.controller;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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

/**
 * Rest Controller for  creating, updating, deleting and getting certificates.
 * <p>
 * Contracts for the verb GET:
 * url/api/certificates?cert=name:like:skydiv,descr:like:airpl&tag=name:like:extr&order=date:desc&page=18&size=11
 * url/api/certificates?cert=name:eq:skydiving&tag=name:eq:extreme&order=date&page=18&size=11
 * <p>
 * Contract for the verbs POST, PUT:
 * url/cert=name:massage,descr:back massage lasting 1 hour,price:123.45,duration:12&tag=name:health,name:beauty
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

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<GiftCertificateDto> create(@ModelAttribute QueryParamsDto paramsDto) {
        GiftCertificateDto created = giftCertificateService.create(paramsDto);
        return buildResponseCreated(created);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GiftCertificateDto update(@ModelAttribute QueryParamsDto paramsDto, @PathVariable Long id) {
        return giftCertificateService.update(paramsDto, id);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<GiftCertificateDto> findByParams(@ModelAttribute("paramsDto") QueryParamsDto paramsDto) {
        return giftCertificateService.findByParams(paramsDto);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<GiftCertificateDto> findAll(@RequestParam(required = false) String page, @RequestParam(required = false) String size) {
        QueryParamsDto dto = new QueryParamsDto();
        dto.setPage(page);
        dto.setSize(size);
        return giftCertificateService.findAll(dto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GiftCertificateDto getById(@PathVariable Long id) {
        return giftCertificateService.findById(id);
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
