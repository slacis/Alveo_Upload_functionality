package upload;


import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import net.sf.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


public class ReadSpreadsheet {

	static HashMap<String, JSONObject> importedMetadata = new HashMap<String, JSONObject>();
	static Map<String, String> META_MAP = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER);
//	private static final String FILE_NAME = "/media/sf_sharedwithvirtual/mq_emotional.xlsx";
	public static final String MAPPING = "data/metadatamappings.xlsx";
//	public static void main(String[] args) {
//
//		readMeta(FILE_NAME, "Recordings", getMap(MAPPING,"Mapping", META_MAP), "mbep", true, true, true, "_");
//
//	}
	public static Map<String, String> getMap(String fileName, String sheetName, Map<String, String> META_MAP ){

		try {

			FileInputStream excelFile = new FileInputStream(new File(fileName));
			Workbook workbook = new XSSFWorkbook(excelFile);
			Sheet datatypeSheet = workbook.getSheet(sheetName);
			Iterator<Row> iterator = datatypeSheet.iterator();

			while (iterator.hasNext()) {

				Row currentRow = iterator.next();
				Iterator<Cell> cellIterator = currentRow.iterator();
				boolean add = false;
				String temp = "";
				while (cellIterator.hasNext()) {

					Cell currentCell = cellIterator.next();

					if (currentCell.getCellTypeEnum() == CellType.STRING) {
						if (add){
							META_MAP.put(temp, currentCell.getStringCellValue());
						}
						System.out.print(currentCell.getStringCellValue() + "--");
						temp = currentCell.getStringCellValue(); 
						add = true;
					}
				}
				System.out.println();



			}

			System.out.println(META_MAP.toString());
			return META_MAP;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return META_MAP;

	}

	public static Map<String, String> readMeta(String fileName, String sheetName, 
			Map<String, String> META_MAP, 
			String prefix, 
			Boolean useFirst, 
			Boolean useSecond, 
			Boolean useThird,
			String delimeter,
			HashMap<String, JSONObject> importedMetadata){

		try {

			FileInputStream excelFile = new FileInputStream(new File(fileName));
			Workbook workbook = new XSSFWorkbook(excelFile);
			Sheet datatypeSheet = workbook.getSheet(sheetName);
			Iterator<Row> iterator = datatypeSheet.iterator();
			ArrayList<String> metaMapping = new ArrayList<String>();
			boolean firstRow = true;
			while (iterator.hasNext()) {
				int x = 1;
				boolean firstColumn = true;
				boolean itemName = true;
				Row currentRow = iterator.next();
				Iterator<Cell> cellIterator = currentRow.iterator();
				JSONObject tempJSON = new JSONObject();
				String temp = "";
				String itemID = "";
				String[] splitItem;
				while (cellIterator.hasNext()) {
					Cell currentCell = cellIterator.next();
//					if (currentCell.getCellTypeEnum() == CellType.STRING) {
						if (firstRow){
							if(META_MAP.containsKey(currentCell.getStringCellValue().replaceAll(" ", "_"))){
								metaMapping.add(META_MAP.get(currentCell.getStringCellValue().replaceAll(" ", "_")));
								//								
							} else {
								metaMapping.add(prefix + ":" + currentCell.getStringCellValue().replaceAll(" ", "_"));
							}
						} else {
							if (itemName){
								if (currentCell.getStringCellValue().equals("")){
									itemName = false;
									continue;
								}
								System.out.println("name1" + currentCell.getStringCellValue());
								
								String itemNameExt = currentCell.getStringCellValue().toString();
								
								String itemNameNoExt = FilenameUtils.removeExtension(itemNameExt);
								System.out.println("ext removed " + itemNameNoExt);
								String[] splitItemNoExt= itemNameNoExt.split("\\" + delimeter);
								if (useFirst) {
									itemID = itemID + splitItemNoExt[0];
								}
								if (useSecond) {
									itemID = itemID + delimeter + splitItemNoExt[1];
								}
								if (useThird) {
									itemID = itemID + delimeter + splitItemNoExt[2];
								}
								itemName = false;
//								System.out.println(itemID);
							} else 
								if (currentCell.getCellTypeEnum() == CellType.STRING) {
								System.out.println("name2" + currentCell.getStringCellValue());
								tempJSON.put(metaMapping.get(x), currentCell.getStringCellValue());
								x++;
								 
							} else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
								System.out.println("name2" + String.valueOf(currentCell.getNumericCellValue()));
								tempJSON.put(metaMapping.get(x), String.valueOf(currentCell.getNumericCellValue()));
								x++;
							}
						}
						//						System.out.print(currentCell.getStringCellValue() + "--");
//						temp = currentCell.getStringCellValue(); 

//					}




				}
				//				System.out.println(tempJSON.toString());
				System.out.println();
				if (!firstRow){
				importedMetadata.put(itemID, tempJSON);
				}
				firstRow = false;

				

			}

			System.out.println(metaMapping.get(1).toString());
			System.out.println(importedMetadata.toString());
			return META_MAP;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return META_MAP;

	}

}


