package dto;

import java.util.List;

public interface ResultList<E> {

    Long getCount();

    Long getPageCount();

	List<E> list();

    boolean next();

    void setPage(Long page);
}
