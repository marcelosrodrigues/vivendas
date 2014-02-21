package dto;

import java.util.List;

public interface ResultList<E> {

	Integer getCount();

	Integer getPageCount();

	List<E> list();
}
