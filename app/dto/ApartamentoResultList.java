package dto;

import java.util.List;

import models.Apartamento;

public class ApartamentoResultList extends AbstractResultList<Apartamento> {

	private final List<Apartamento> apartamentos;
	private final Long count;
	private final Long pageCount;

	public ApartamentoResultList(List<Apartamento> apartamentos, Long count,
                                 Long pagecount) {
		this.apartamentos = apartamentos;
		this.count = count;
		this.pageCount = pagecount;
	}

	@Override
	public Long getCount() {
		return count;
	}

	@Override
	public Long getPageCount() {
		return pageCount;
	}

	@Override
	public List<Apartamento> list() {
		return apartamentos;
	}

}
