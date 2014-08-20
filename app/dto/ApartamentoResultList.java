package dto;

import models.Apartamento;

import org.hibernate.Criteria;

import annotations.PageSize;

@PageSize(50)
public class ApartamentoResultList extends AbstractResultList<Apartamento> {

	public ApartamentoResultList(final Criteria criteria) {
        super(criteria);
	}

}
