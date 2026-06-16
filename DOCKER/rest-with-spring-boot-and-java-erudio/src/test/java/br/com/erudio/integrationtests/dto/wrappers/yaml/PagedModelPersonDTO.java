package br.com.erudio.integrationtests.dto.wrappers.yaml;

import br.com.erudio.integrationtests.dto.PersonDTOV1;

import java.util.List;
import java.util.Objects;

public class PagedModelPersonDTO {
    private List<PersonDTOV1> content;
    private PageInfo page;

    public PagedModelPersonDTO() {
    }

    public List<PersonDTOV1> getContent() {
        return content;
    }

    public void setContent(List<PersonDTOV1> content) {
        this.content = content;
    }

    public PageInfo getPage() {
        return page;
    }

    public void setPage(PageInfo page) {
        this.page = page;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PagedModelPersonDTO that = (PagedModelPersonDTO) o;
        return Objects.equals(content, that.content) && Objects.equals(page, that.page);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, page);
    }

    public static class PageInfo {
        private int size;
        private long totalElements;
        private int totalPages;
        private int number;

        public PageInfo() {
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            PageInfo pageInfo = (PageInfo) o;
            return size == pageInfo.size && totalElements == pageInfo.totalElements && totalPages == pageInfo.totalPages && number == pageInfo.number;
        }

        @Override
        public int hashCode() {
            return Objects.hash(size, totalElements, totalPages, number);
        }
    }

}
