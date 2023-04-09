package ru.clevertec.ecl.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.ecl.service.TagService;
import ru.clevertec.ecl.service.dto.TagDto;


@RestController
@RequestMapping("api/tags")
@RequiredArgsConstructor
public class RestTagController {

    private final TagService tagService;

    /**
     * endpoint to get the most widely used tag with the highest user order value
     * Mapping example:
     * url/api/tags?user=1
     */
    @GetMapping
    public TagDto findPopularTagMaxTotalCostOrder(@RequestParam(value = "user") Long userId) {
        return tagService.findPopularTag(userId);
    }
}
