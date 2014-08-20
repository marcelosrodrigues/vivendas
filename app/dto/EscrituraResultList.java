package dto;

import models.Escritura;

import org.hibernate.Criteria;

import annotations.PageSize;

/**
 * Created by Marceloo on 11/07/2014.
 */
@PageSize(5)
public class EscrituraResultList extends AbstractResultList<Escritura> {

    public EscrituraResultList(final Criteria criteria) {

        super(criteria);

    }

}
