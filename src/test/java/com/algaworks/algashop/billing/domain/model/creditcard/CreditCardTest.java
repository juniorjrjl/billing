package com.algaworks.algashop.billing.domain.model.creditcard;

import com.algaworks.algashop.billing.utility.databuilder.domain.CreditCardDataBuilder;
import com.algaworks.algashop.billing.utility.CustomFaker;
import com.algaworks.algashop.billing.utility.tag.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

@UnitTest
class CreditCardTest {

    private static final CustomFaker customFaker = CustomFaker.getInstance();

    @BeforeEach
    void setup(){
        CustomFaker.getInstance().reseed();
    }

    @Test
    void shouldCreateCreditCard(){
        final var actual = CreditCardDataBuilder.builder().build();
        assertThat(actual).hasNoNullFieldsOrProperties();
    }

    private static final List<Arguments> givenInvalidParamsWhenCreatingCreditCardThenThrowException =
            List.of(
                    Arguments.of(
                            "",
                            customFaker.subscription().paymentMethods(),
                            customFaker.number().numberBetween(1, 13),
                            customFaker.number().digits(4)
                    ),
                    Arguments.of(
                            " ",
                            customFaker.subscription().paymentMethods(),
                            customFaker.number().numberBetween(1, 13),
                            customFaker.number().digits(4)
                    ),
                    Arguments.of(
                            customFaker.number().digits(4),
                            "",
                            customFaker.number().numberBetween(1, 13),
                            customFaker.number().digits(4)
                    ),
                    Arguments.of(
                            customFaker.number().digits(4),
                            " ",
                            customFaker.number().numberBetween(1, 13),
                            customFaker.number().digits(4)
                    ),
                    Arguments.of(
                            customFaker.number().digits(4),
                            customFaker.subscription().paymentMethods(),
                            0,
                            customFaker.number().digits(4)
                    ),
                    Arguments.of(
                            customFaker.number().digits(4),
                            customFaker.subscription().paymentMethods(),
                            13,
                            customFaker.number().digits(4)
                    ),
                    Arguments.of(
                            customFaker.number().digits(4),
                            customFaker.subscription().paymentMethods(),
                            customFaker.number().numberBetween(1, 13),
                            ""
                    ),
                    Arguments.of(
                            customFaker.number().digits(4),
                            customFaker.subscription().paymentMethods(),
                            customFaker.number().numberBetween(1, 13),
                            " "
                    )
            );

    @ParameterizedTest
    @FieldSource
    void givenInvalidParamsWhenCreatingCreditCardThenThrowException(final String lastNumbers,
                                                                    final String brand,
                                                                    final Integer expirationMonth,
                                                                    final String gatewayCode){
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> CreditCard.brandNew(
                        UUID.randomUUID(),
                        lastNumbers,
                        brand,
                        expirationMonth,
                        customFaker.number().numberBetween(30, 50),
                        gatewayCode
                ));
    }

    @Test
    void shouldSetGatewayCode(){
        final var creditCard = CreditCardDataBuilder.builder().build();
        final var gatewayCode = customFaker.number().digits(4);
        creditCard.setGatewayCode(gatewayCode);
        assertThat(creditCard.getGatewayCode()).isEqualTo(gatewayCode);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { " "})
    void givenInvalidGatewayCodeWhenSetGatewayCodeThenThrowException(final String gatewayCode){
        final var creditCard = CreditCardDataBuilder.builder().build();
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> creditCard.setGatewayCode(gatewayCode));
    }

}