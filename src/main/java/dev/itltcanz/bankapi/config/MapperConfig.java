package dev.itltcanz.bankapi.config;

import dev.itltcanz.bankapi.dto.card.CardDtoResponse;
import dev.itltcanz.bankapi.dto.request.BlockRequestDtoResponse;
import dev.itltcanz.bankapi.dto.transaction.TransactionDtoResponse;
import dev.itltcanz.bankapi.entity.BlockRequest;
import dev.itltcanz.bankapi.entity.Card;
import dev.itltcanz.bankapi.entity.Transaction;
import dev.itltcanz.bankapi.util.HibernateUtils;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for ModelMapper to map entities to DTOs with custom mappings.
 */
@Configuration
public class MapperConfig {

  /**
   * Configures and returns a ModelMapper bean with strict matching and custom converters.
   *
   * @return configured ModelMapper instance
   */
  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT)
        .setSkipNullEnabled(true);

    // Converter for masking card numbers
    Converter<String, String> cardNumberMasker = ctx -> {
      String source = ctx.getSource();
      if (source == null || source.length() < 4) {
        return source;
      }
      return "************" + source.substring(source.length() - 4);
    };

    // Card to CardDtoResponse mapping
    modelMapper.typeMap(Card.class, CardDtoResponse.class).addMappings(mapper -> {
      mapper.map(src -> HibernateUtils.getIdAsString(src.getOwner()), CardDtoResponse::setOwnerId);
      mapper.using(cardNumberMasker).map(Card::getNumber, CardDtoResponse::setNumber);
    });

    // Transaction to TransactionDtoResponse mapping
    modelMapper.typeMap(Transaction.class, TransactionDtoResponse.class).addMappings(mapper -> {
      mapper.map(HibernateUtils::getIdAsString, TransactionDtoResponse::setId);
      mapper.using(cardNumberMasker).map(src -> HibernateUtils.getCardNumber(src.getSenderCard()),
          TransactionDtoResponse::setSenderCardId);
      mapper.using(cardNumberMasker).map(src -> HibernateUtils.getCardNumber(src.getReceiverCard()),
          TransactionDtoResponse::setReceiverCardId);
    });

    // BlockRequest to BlockRequestDtoResponse mapping
    modelMapper.typeMap(BlockRequest.class, BlockRequestDtoResponse.class).addMappings(mapper -> {
      mapper.using(cardNumberMasker).map(src -> HibernateUtils.getCardNumber(src.getCard()),
          BlockRequestDtoResponse::setCardId);
      mapper.map(src -> HibernateUtils.getIdAsString(src.getUser()),
          BlockRequestDtoResponse::setUserId);
      mapper.map(src -> HibernateUtils.getIdAsString(src.getAdmin()),
          BlockRequestDtoResponse::setAdminId);
    });

    return modelMapper;
  }
}
