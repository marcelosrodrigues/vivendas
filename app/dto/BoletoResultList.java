package dto;

import models.Boleto;

import org.hibernate.Criteria;

import annotations.PageSize;

@PageSize(50)
public class BoletoResultList extends AbstractResultList<Boleto> {

	public BoletoResultList(final Criteria criteria) {
		super(criteria);
	}

}
