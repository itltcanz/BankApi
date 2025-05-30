package dev.itltcanz.bankapi.config;

import dev.itltcanz.bankapi.dto.card.CardDtoResponse;
import dev.itltcanz.bankapi.entity.Card;
import org.hibernate.Hibernate;
import org.modelmapper.AbstractConverter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
            .setMatchingStrategy(MatchingStrategies.STRICT)
            .setSkipNullEnabled(true);

        // Конвертер для ownerId
        AbstractConverter<Card, String> ownerIdConverter = new AbstractConverter<>() {
            @Override
            protected String convert(Card source) {
                if (source == null || source.getOwner() == null || !Hibernate.isInitialized(source.getOwner())) {
                    return null;
                }
                return source.getOwner().getId().toString();
            }
        };

        // Конвертер для маскирования номера карты
        AbstractConverter<String, String> cardNumberMasker = new AbstractConverter<>() {
            @Override
            protected String convert(String source) {
                if (source == null || source.length() < 4) {
                    return source;
                }
                return "************" + source.substring(source.length() - 4);
            }
        };

        var cardToDtoTypeMap = modelMapper.createTypeMap(Card.class, CardDtoResponse.class);
        cardToDtoTypeMap.addMappings(mapper -> {
            mapper.using(ownerIdConverter).map(src -> src, CardDtoResponse::setOwnerId);
            mapper.using(cardNumberMasker).map(Card::getNumber, CardDtoResponse::setNumber);
            mapper.map(Card::getValidityPeriod, CardDtoResponse::setValidityPeriod);
            mapper.map(Card::getStatus, CardDtoResponse::setStatus);
            mapper.map(Card::getBalance, CardDtoResponse::setBalance);
        });

        return modelMapper;
    }
}
