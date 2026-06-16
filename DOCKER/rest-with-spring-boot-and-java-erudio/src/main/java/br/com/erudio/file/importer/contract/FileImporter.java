package br.com.erudio.file.importer.contract;

import br.com.erudio.data.dto.v1.PersonDTOV1;

import java.io.InputStream;
import java.util.List;

public interface FileImporter {

    List<PersonDTOV1> importFile(InputStream inputStream) throws Exception;
}
