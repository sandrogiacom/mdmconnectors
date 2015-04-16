package com.totvslabs.mdm.client.pojoTSA;

import java.io.Serializable;

public class UserSimple implements Serializable {
	private static final long serialVersionUID = 1L;
	private String cardCode;

	public UserSimple(String cardCode) {
		super();
		this.cardCode = cardCode;
	}

	public String getCardCode() {
		return cardCode;
	}
}
