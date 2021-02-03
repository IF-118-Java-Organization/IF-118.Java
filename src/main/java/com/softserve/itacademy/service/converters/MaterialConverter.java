package com.softserve.itacademy.service.converters;

import com.softserve.itacademy.entity.Material;
import com.softserve.itacademy.response.MaterialResponse;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@AllArgsConstructor
@Component
public class MaterialConverter {

    private final ModelMapper mapper;

    public MaterialResponse of(Material material) {
        return mapper.map(material, MaterialResponse.class);
    }

}
