package ru.clevertec.ecl.web.controller;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.clevertec.ecl.service.GiftCertificateService;
import ru.clevertec.ecl.service.dto.GiftCertificateDto;
import ru.clevertec.ecl.service.dto.QueryParamsDto;
import ru.clevertec.ecl.service.util.serializer.Serializer;

@RestController
@RequestMapping("api/certificates")
@RequiredArgsConstructor
public class RestCertificateController {

    private final GiftCertificateService giftCertificateService;
    private final Serializer serializer;

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
    public ResponseEntity<String> create(@ModelAttribute QueryParamsDto paramsDto) {
        GiftCertificateDto created = giftCertificateService.create(paramsDto);
        return buildResponseCreated(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@ModelAttribute QueryParamsDto paramsDto, @PathVariable Long id) {
        GiftCertificateDto updated = giftCertificateService.update(paramsDto, id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(serializer.serialize(updated), headers, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<String> get(@ModelAttribute("paramsDto") QueryParamsDto paramsDto) {
        List<GiftCertificateDto> list = giftCertificateService.find(paramsDto);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(serializer.serialize(list), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getById(@PathVariable Long id) {
        GiftCertificateDto dto = giftCertificateService.findById(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<>(serializer.serialize(dto), headers, HttpStatus.OK);
    }

    private ResponseEntity<String> buildResponseCreated(GiftCertificateDto created) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .location(getLocation(created))
                .body(serializer.serialize(created));
    }

    private URI getLocation(GiftCertificateDto created) {
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("/certificates/{id}")
                .buildAndExpand(created.getId())
                .toUri();
    }
}
