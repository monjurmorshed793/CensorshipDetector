package org.censorship.service;

import org.apache.poi.ss.usermodel.*;
import org.censorship.domain.WebAddress;
import org.censorship.repository.WebAddressRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Transactional
public class ExcelWebAddressFileExtractionService {

    private final Logger log = LoggerFactory.getLogger(ExcelWebAddressFileExtractionService.class);

    private WebAddressRepository webAddressRepository;

    public ExcelWebAddressFileExtractionService(WebAddressRepository webAddressRepository) {
        this.webAddressRepository = webAddressRepository;
    }

    public void extractExcelData(File file)throws Exception{

        // firstly delete all the data, as uploading via excel file
        webAddressRepository.deleteAll();

        Workbook workbook = WorkbookFactory.create(file);
        List<WebAddress> webAddressList = new ArrayList<>();

        Sheet sheet = workbook.getSheetAt(0);

        Iterator<Row> rows = sheet.iterator();

        while(rows.hasNext()){
            Row row = rows.next();
            Cell cell = row.getCell(0);
            WebAddress webAddress = new WebAddress();
            webAddress.setName(cell.getStringCellValue());
            webAddressList.add(webAddress);
        }

        webAddressRepository.saveAll(webAddressList);
    }

}
