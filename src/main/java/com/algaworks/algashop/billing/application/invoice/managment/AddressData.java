package com.algaworks.algashop.billing.application.invoice.managment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressData {
	private String street;
	private String number;
	@Nullable
	private String complement;
	private String neighborhood;
	private String city;
	private String state;
	private String zipCode;
}