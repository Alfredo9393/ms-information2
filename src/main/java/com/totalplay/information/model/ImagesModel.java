/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.totalplay.information.model;

import lombok.Data;

/**
 *
 * @author APerez
 */
@Data
public class ImagesModel {
    public String getIdcommerce() {
		return idcommerce;
	}

	public void setIdcommerce(String idcommerce) {
		this.idcommerce = idcommerce;
	}

	private String idcommerce;
}
