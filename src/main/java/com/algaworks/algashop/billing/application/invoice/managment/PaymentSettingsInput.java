package com.algaworks.algashop.billing.application.invoice.managment;

import com.algaworks.algashop.billing.domain.model.invoice.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

import java.util.UUID;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSettingsInput {
	private PaymentMethod method;
	@Nullable
	private UUID creditCardId;
}