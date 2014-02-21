package dto;

import java.util.List;

import models.Apartamento;

public class ApartamentoResultList implements ResultList<Apartamento> {

	private final List<Apartamento> apartamentos;
	private final Integer count;
	private final Integer pageCount;

	public ApartamentoResultList(List<Apartamento> apartamentos, int count,
			int pagecount) {
		this.apartamentos = apartamentos;
		this.count = count;
		this.pageCount = pagecount;
	}

	@Override
	public Integer getCount() {
		return count;
	}

	@Override
	public Integer getPageCount() {
		return pageCount;
	}

	@Override
	public List<Apartamento> list() {
		return apartamentos;
	}

}
