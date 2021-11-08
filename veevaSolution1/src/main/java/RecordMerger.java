/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jananimanoharan
**/
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RecordMerger {
    public static final String FILENAME_toSort = "toSort.csv";
    public static final String FILENAME_COMBINED = "combined.csv";
    public static void main(String[] args) throws FileNotFoundException, IOException {
   
   
       StringBuilder html = new StringBuilder();
       Document htmlDoc = null;
       ArrayList<String> header = new ArrayList<String>();
       ArrayList<String> id = new ArrayList<String>();
       ArrayList<String> name = new ArrayList<String>();
       ArrayList<String> address = new ArrayList<String>();
       ArrayList<String> phone = new ArrayList<String>();
       List<List<String>> csvData = new ArrayList<List<String>>();
       
         
        try{
            
            htmlDoc = Jsoup.parse(new File("first.html"), "ISO-8859-1");
            String title = htmlDoc.title();
            Element table = htmlDoc.selectFirst("table");
            
            Iterator<Element> row = table.select("tr").iterator();
            
            //row.next(); //Skipping the <th>
            //while (row.next())
            {
            Iterator<Element> ite0 = ((Element)row.next()).select("th").iterator();
            header.add(ite0.next().text());
            header.add(ite0.next().text());
            header.add(ite0.next().text());
            header.add(ite0.next().text());
            }
                    
            while (row.hasNext())
            {
                //Iterator<Element> ite0 = ((Element)row.next()).select("th").iterator();
            	Iterator<Element> ite = ((Element)row.next()).select("td").iterator();
                //System.out.print("ID " + ite.next().text() + " ");
                //header.add(ite0.next().text()+ " ");
                id.add(ite.next().text());
                name.add(ite.next().text());
                address.add(ite.next().text());
                phone.add(ite.next().text());
                
            }
            System.out.println(header);
            System.out.println(id);
            System.out.println(name);
            System.out.println(address);
            System.out.println(phone);
            
        }catch (IOException e) {
            e.printStackTrace();
        }
        
        //Reading data from second.csv file
        String fileName = "second.csv";
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            String[] values = null;
            while ((values = reader.readNext()) != null) {
                csvData.add(Arrays.asList(values));      
            }
            System.out.println(csvData);
        } catch (CsvException ex) {
                Logger.getLogger(RecordMerger.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(csvData.size());
        System.out.println(csvData.get(0).get(0));
       

        //Combining columns ID and Name from html and csv file together into one array
        for(int i=1; i<csvData.size();i++){
                //new set of id array
            id.add(csvData.get(i).get(3));     
        }
        System.out.println("id "+id);
        for(int i=1; i<csvData.size();i++){
                //new set of name array
            name.add(csvData.get(i).get(1)); 
        }
        System.out.println("name "+name);
            
             //combining both html and csv column names 
       
        for(int j = 0;j<csvData.get(0).size();j++){
            header.add(csvData.get(0).get(j));
        }
        LinkedHashSet<String> hashSet = new LinkedHashSet<>(header);
        ArrayList<String> headerWithoutDuplicates = new ArrayList<>(hashSet);
        System.out.println("header "+headerWithoutDuplicates);
        //removing duplicates from header
        //Creating the file output file
        FileWriter fw = new FileWriter(FILENAME_toSort);
        BufferedWriter bw = new BufferedWriter(fw);
      
        for(int i=0;i<headerWithoutDuplicates.size();i++)
        {
            bw.write(headerWithoutDuplicates.get(i)+",");  
        }
        for(int i=0;i<id.size();i++)
        {
            bw.newLine();
            
            bw.write(id.get(i)+","+name.get(i)+",");
            if(i<address.size())
            {
                bw.write(address.get(i)+",");
            }
            else
            {
                bw.write("null"+","); 
            }
            if(i<phone.size())
            {
                bw.write(phone.get(i)+",");
            }
            else
            {
                bw.write("null"+","); 
            }
            
            if(i<phone.size())
            {
               bw.write("null"+","+"null"+",");
            }
            else    
            {
                if(i>=phone.size() && i<id.size()){
                    bw.write(csvData.get(i-1).get(0)+","+csvData.get(i-1).get(2)+",");
                }    
               
            }        
            
        }
        
        bw.close();
        fw.close();
        //Data Sorting from toSort.csv to combined.csv creating a new Arraylist
        BufferedReader reader = new BufferedReader(new FileReader(FILENAME_toSort));
        Map<String, List<String>> map = new TreeMap<String, List<String>>();
        String line = reader.readLine();//read header
        while ((line = reader.readLine()) != null) {
            String key = getField(line);
            List<String> l = map.get(key);
            if (l == null) {
                l = new LinkedList<String>();
                map.put(key, l);
            }
            l.add(line);

        }
        reader.close();
        FileWriter writer = new FileWriter(FILENAME_COMBINED);
        writer.write("ID, Name,Address,Phone,Occupation,Gender\n");
        for (List<String> list : map.values()) {
            for (String val : list) {
                writer.write(val);
                writer.write("\n");
            }
        }
        writer.close();
    }

    private static String getField(String line) {
        return line.split(",")[0];// extract value you want to sort on
    }
        
        
        
   } 

   

