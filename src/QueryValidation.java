import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class QueryValidation {

    private static String delimiter = "--";
    private static String delimiter1 = "#";
    private static String delimiter2 = "!!";
    private static String delimiter3 = "&&";

    /**
     * This method determines the type of SQL query and then executes the appropriate method.
     */
    public void queryType() {
        Scanner input = new Scanner(System.in);
//        System.out.println("Enter the Query");
//        String query = input.nextLine();
        String query;
        boolean shouldContinue = true;

        while (shouldContinue) {
            System.out.println("Please Enter the Query:");
            query = input.nextLine();

            String queryTOlowerCase = query.toLowerCase();
            query = query.toLowerCase();
        String[] splitQ = query.trim().toLowerCase().split(" ");
        if (splitQ[0].startsWith("select")) {
            select(queryTOlowerCase);
        } else if (splitQ[0].startsWith("create") && splitQ[1].startsWith("database")) {
            createDb(queryTOlowerCase);
        } else if (splitQ[0].startsWith("create")) {
            createTable(queryTOlowerCase);
        } else if (splitQ[0].startsWith("drop") && splitQ[1].startsWith("database")) {
            dropDb(queryTOlowerCase);
        } else if (splitQ[0].startsWith("drop")) {
            dropTable(queryTOlowerCase);
        } else if (splitQ[0].startsWith("truncate")) {
            truncateTable(queryTOlowerCase);
        } else if (splitQ[0].startsWith("insert")) {
            String m = insertQuery(queryTOlowerCase);
            System.out.println(m);
        } else if (splitQ[0].startsWith("update")) {
            updateQuery(queryTOlowerCase);
        } else if (splitQ[0].startsWith("delete")) {
            deleteRow(queryTOlowerCase);
        } else{
            System.out.println("Invalid Query Please try again");
        }


            System.out.println("Do you want to continue? (Y/N)");
            query = input.nextLine();

            if (!query.equalsIgnoreCase("Y")) {
                shouldContinue = false;
            }
        }

        input.close();

    }



    /**
     * This method creates a Database.
     * @param q is the query entered by the user
     */
    public void createDb(String q) {
        try {
            boolean queryExist = true;
            String[] splitDb = q.trim().toLowerCase().replace(";", "").split(" ");
            String DbName = splitDb[2];
            File myObj = new File("DatabaseN.txt");
            Scanner myReader = new Scanner(myObj);
            if (myReader.hasNextLine()) {
                System.out.println("Cannot create a Database as one already exists|| Drop it and then create");
            } else {
                System.out.println("Database created");
                FileWriter myWriter = new FileWriter("DatabaseN.txt", true);
                myWriter.write(DbName);
                myWriter.close();
            }

        } catch (Exception e) {
            System.out.println("error while writing");
        }

    }

    /**
     * This method is used to drop a Database.
     * @param q is the query entered by the user
     */
    public void dropDb(String q) {
        try {
            String[] splitDb = q.trim().toLowerCase().replace(";", "").split(" ");
            String DbName = splitDb[2];
            File myObj = new File("DatabaseN.txt");
            Scanner myReader = new Scanner(myObj);
            String check = myReader.nextLine();
            if (check.equals(DbName)) {
                FileWriter w = new FileWriter("DatabaseN.txt");
                w.write("");
                w.close();
                System.out.println("Database Dropped Succesfully");
            }

        } catch (Exception e) {
            System.out.println("Database Doesnt Exsist");
        }

    }

    /**
     * This method is used to drop a table from the Database.
     * @param q is the query entered by the user
     */
    public void dropTable(String q) {
        try {
            String[] splitTb = q.trim().toLowerCase().replace(";", "").split(" ");
            String TbDelete = splitTb[2];
            System.out.println("Table you want to delete is " +TbDelete);
            Path TableToDelete = Paths.get("TableName-" + TbDelete + ".txt");
            Files.delete(TableToDelete);
            System.out.println("Table Deleted");


        } catch (Exception e) {
            System.out.println("Table Does not Exist");
        }

    }

    /**
     * This method is used to truncate a table.
     * @param q is the query entered by the user
     */
    public void truncateTable(String q) {
        try {
            String[] splitTb = q.trim().toLowerCase().replace(";", "").split(" ");
            String TbName = splitTb[2];
            System.out.println("Table you want to truncate is \t" +TbName);
            File myObj = new File("TableName-" + TbName + ".txt");
            if(myObj.exists()){
                FileWriter w = new FileWriter("TableName-" + TbName + ".txt");
                w.write("");
                w.close();
                System.out.println("Table truncated successfully");
            }else {
                System.out.println("Table Doesn't Exist");
            }


        } catch (Exception e) {

        }

    }

    /**
     * This method is used to delete a row in the table.
     * @param q  is the query entered by the user
     * @return This method return a boolean whether a row is successfully deleted or not
     */
    public Boolean deleteRow(String q){
        try {
            q = q.replace(";","").replace("'","").replace("\"","");
            String[] splitTb = q.split("\\s+");
            String tableName = splitTb[2];
            String[] Condition = (q.trim().split("where")[1].split("!=|<=|>=|>|<|="));
            for (int i =0; i < Condition.length; i++){
                Condition[i] = Condition[i].trim();
            }
            String colName = Condition[0].trim();
            String valueToChange = Condition[1].trim();
            String operator = q.trim().split(colName)[1].trim().split(valueToChange)[0];
            ArrayList<ArrayList<String>> data = getTable(tableName);
            ArrayList<ArrayList<String>> metaData = getMeta(tableName);
            String pk = metaData.get(metaData.size()-1).get(0);
            HashMap<String, HashMap<String, String>> hashmap = new LinkedHashMap<String, HashMap<String, String>>();
            for (int i=0;i< data.size(); i++){
                HashMap<String, String> tempMap= new LinkedHashMap<String, String>();
                String primaryKey=" ";
                for(int j=0;j< data.get(i).size(); j++){
                    tempMap.put(metaData.get(j).get(0),data.get(i).get(j));
                    if(metaData.get(j).get(0).equals(pk)){
                        primaryKey=data.get(i).get(j);
                    }
                }
                hashmap.put(primaryKey,tempMap);
            }

            Set<String> colNames = new LinkedHashSet<String>();
            for(int i=0; i<metaData.size(); i++){
                for(int j=0;j<metaData.get(i).size(); j++){
                    colNames.add(metaData.get(i).get(0));
                }
            }
            if(Condition[0] != null){
                if(!colNames.contains(Condition[0])){
                    System.out.println("Invalid column name '" + Condition[0]+"' in table \"" + tableName+"\"");
                    return false;
                }
            }

            String[] colNamesList = colNames.toArray(new String[colNames.size()]);

            HashMap<String, HashMap<String, Object>> outerMap = new HashMap<String, HashMap<String, Object>>();
            HashMap<String, Object> innerMap = new HashMap<String, Object>();
            innerMap.put(pk,data);
            outerMap.put(pk, innerMap);
            HashMap<String, HashMap<String, String>> resultSet1 = new LinkedHashMap<String, HashMap<String, String>>();

            for (Map.Entry<String, HashMap<String, String>> i : hashmap.entrySet()) {
                HashMap<String, String> innerResultSet = i.getValue();
                for (int k = 0; k < colNames.size(); k++) {

                    for (Map.Entry<String, String> in : innerResultSet.entrySet()) {
                        if(Condition[0] != null) {
                            if (operator.equals("!=")) {
                                if (in.getKey().equals(Condition[0]) && !in.getValue().equals(Condition[1].replace("\"", ""))) {
                                    resultSet1.put(i.getKey(), i.getValue());
                                }
                            } else if (operator.equals(">=")) {
                                if (in.getKey().equals(Condition[0]) && Integer.parseInt(in.getValue()) >= Integer.parseInt(Condition[1])) {
                                    resultSet1.put(i.getKey(), i.getValue());
                                }
                            } else if (operator.equals("<=")) {
                                if (in.getKey().equals(Condition[0]) && Integer.parseInt(in.getValue()) <= Integer.parseInt(Condition[1])) {
                                    resultSet1.put(i.getKey(), i.getValue());
                                }
                            } else if (operator.equals("=")) {
                                if (in.getKey().equals(Condition[0]) && in.getValue().equals(Condition[1].replace("\"", ""))) {
                                    resultSet1.put(i.getKey(), i.getValue());
                                }
                            } else if (operator.equals(">")) {
                                if (in.getKey().equals(Condition[0]) && Integer.parseInt(in.getValue()) > Integer.parseInt(Condition[1])) {
                                    resultSet1.put(i.getKey(), i.getValue());
                                }
                            } else if (operator.equals("<")) {
                                if (in.getKey().equals(Condition[0]) && Integer.parseInt(in.getValue()) < Integer.parseInt(Condition[1])) {
                                    resultSet1.put(i.getKey(), i.getValue());
                                }
                            }
                        }
                        else
                            resultSet1.put(i.getKey(), i.getValue());
                    }
                }
            }
            for (Map.Entry<String, HashMap<String, String>> i : resultSet1.entrySet()) {
                hashmap.remove(i.getKey());
            }
            String ans = "";
            for (Map.Entry<String, HashMap<String, String>> i : hashmap.entrySet()) {
                for (Map.Entry<String, String> j : i.getValue().entrySet()) {
                    ans += j.getValue() + "&&";
                }
                ans = ans.substring(0,ans.length()-2) + "\n";
            }
            FileWriter w = new FileWriter("TableName-" + tableName + ".txt");
            w.write(ans);
            w.close();
            System.out.println("record deleted successfully");
        return true;

        }catch (Exception e){
            return false;
        }
    }

    /**
     * This method is used to create a table.
     * @param q is the query entered by the user
     */
    public void createTable(String q) {
        try {
            String[] splitQu = q.trim().split(" ");
            System.out.println("Table created successfully");
            String tableName = splitQu[2];
            String[] metaValues = q.trim().split(tableName)[1].replace("(", "").replace(")", "").replace(";", "").replace("not null", "notnull").trim().split(",");
            String allEle = tableName + delimiter;
            String writeData = new String();
            for (String eachElement : metaValues) {
                String[] dataType = eachElement.trim().split(",");
                for (String eachElement1 : dataType) {
                    String[] columnType = eachElement1.trim().split(" ");
                    for (int i = 0; i < columnType.length; i++) {
                        allEle += columnType[i];
                        if (i == columnType.length - 1)
                            allEle += delimiter1;
                        else
                            allEle += delimiter;
                    }


                }

            }
            FileWriter myWriter = new FileWriter("metadata.txt", true);
            myWriter.write(allEle + "\n");
            myWriter.close();

            File myfile = new File("TableName-" + tableName + ".txt");
            myfile.createNewFile();


        } catch (Exception e) {
            System.out.println("error");
        }

    }

    /**
     * This method is used to Insert values into the table.
     * @param q is the query entered by the user
     * @return It returns a message indicating success or failure.
     */
    public String insertQuery(String q){
        try{
        q = q.replace(";","");
        String[] splitTb = q.split("\\s+");
        String tableName = splitTb[2];
        String[] columns1 = q.replace("insert", "").replace(tableName,"").replace("into","").split("values");
        String[] colNames = columns1[0].replace("(","").trim().replace(")","").split(",");
        for (int i = 0; i < colNames.length; i++) {
            colNames[i] = colNames[i].trim();
        }
         ArrayList<ArrayList<String>> meta = getMeta(tableName);
//            System.out.println();
        ArrayList<String> colNameList = new ArrayList<String>(Arrays.asList(colNames));
            String[] valuesToInsert = columns1[1].replace("(","").trim().replace(")","").replace("'","").split(",");
        ArrayList<String> ans = new ArrayList<String>();

        if(colNameList != null && colNameList.size() > 0 && !colNameList.get(0).equals("")){
            for(int i=0 ; i < meta.size()-1; i++){
                int p = colNameList.indexOf(meta.get(i).get(0));
                if(p == -1){
                    if(meta.get(i).get(2).equals("notnull"))
                        return "required values not specified";
                    else
                        ans.add("null");
                }
                else{
                    if(meta.get(i).get(1).equals("int") && (valuesToInsert[p].contains("\"") || valuesToInsert[p].contains("\'"))){
                        return "invalid value type";
                    }
                    ans.add(valuesToInsert[p]);

                }
            }
        }else{
       ans = new ArrayList<String>(Arrays.asList(valuesToInsert));
      }


        String stringToInsert = ans.toString().replace("[","").replace("]","").replace(",  ","&&").replace(", ","&&").replace(",","&&");


        File myObj = new File("TableName-" + tableName + ".txt");
        if(myObj.exists()){
            FileWriter w = new FileWriter("TableName-" + tableName + ".txt",true);
            w.write(stringToInsert + "\n");
            w.close();
            return "Values Inserted successfully";
        }else {
            return "Table Doesn't Exist";
        }

    }catch (Exception e){
        return "error";
        }
    }

    /**
     * This method is used to update the values into the table.
     * @param q is the query entered by the user
     * @return This method return a boolean whether the operation was successful or not
     */
    public Boolean updateQuery(String q) {
        try{
            q = q.replace(";","");
//            System.out.println(q);
            String tbName = q.replace("update","").trim().split("set")[0].trim();
//            System.out.println(tbName);
            String condition = q.replace("update","").trim().replace(tbName,"").replace("set","").replace("'","").split("where")[0];
//            System.out.println(condition);
            String leftColName = condition.trim().split("=")[0].trim();
//            System.out.println(leftColName);
            String leftvalueToUpdate = condition.trim().split("=")[1].trim().replace("\"","").replace("\'","");
//            System.out.println(leftvalueToUpdate);
//            System.out.println(condition);
            String whereCondition = q.replace("'","").split("where")[1];
//            System.out.println(whereCondition);
            String RightColName = whereCondition.trim().split("!=|<=|>=|>|<|=")[0].trim();
            String RightValueToUpdate = whereCondition.trim().split("!=|<=|>=|>|<|=")[1].trim().replace("\"","").replace("\'","");
            String operator = q.trim().split(RightColName)[1].trim().replace("'","").replace("\"","").split(RightValueToUpdate)[0].trim();

            ArrayList<ArrayList<String>> data = getTable(tbName);
            ArrayList<ArrayList<String>> metaData = getMeta(tbName);
            String pk = metaData.get(metaData.size()-1).get(0);
    //        System.out.println(pk);
            HashMap<String, HashMap<String, String>> hashmap = new LinkedHashMap<String, HashMap<String, String>>();
            for (int i=0;i< data.size(); i++){
                HashMap<String, String> tempMap= new LinkedHashMap<String, String>();
                String primaryKey=" ";
                for(int j=0;j< data.get(i).size(); j++){
                    tempMap.put(metaData.get(j).get(0),data.get(i).get(j));
                    if(metaData.get(j).get(0).equals(pk)){
                        primaryKey=data.get(i).get(j);
                    }
                }
                hashmap.put(primaryKey,tempMap);
            }


            Set<String> colNames = new LinkedHashSet<String>();
            for(int i=0; i<metaData.size(); i++){
                for(int j=0;j<metaData.get(i).size(); j++){
                    colNames.add(metaData.get(i).get(0));
                }
            }
            if(RightColName != null){
                if(!colNames.contains(RightColName)){
                    System.out.println("Invalid column name '" + RightColName+"' in table \"" + tbName+"\"");
                    return false;
                }
            }
            if(leftColName != null){
                if(!colNames.contains(leftColName)){
                    System.out.println("Invalid column name '" + leftColName+"' in table \"" + tbName+"\"");
                    return false;
                }
            }

            String[] colNamesList = colNames.toArray(new String[colNames.size()]);

            HashMap<String, HashMap<String, Object>> outerMap = new HashMap<String, HashMap<String, Object>>();
            HashMap<String, Object> innerMap = new HashMap<String, Object>();
            innerMap.put(pk,data);
            outerMap.put(pk, innerMap);
            HashMap<String, HashMap<String, String>> resultSet1 = new LinkedHashMap<String, HashMap<String, String>>();

            for (Map.Entry<String, HashMap<String, String>> i : hashmap.entrySet()) {
                HashMap<String, String> innerResultSet = i.getValue();
                for (int k = 0; k < colNames.size(); k++) {

                    for (Map.Entry<String, String> in : innerResultSet.entrySet()) {
                        if(RightColName != null) {

                            if (operator.equals("!=")) {
                                if (in.getKey().equals(RightColName) && !in.getValue().equals(RightValueToUpdate.replace("\"", ""))) {
                                    innerResultSet.put(leftColName,leftvalueToUpdate);
                                    resultSet1.put(i.getKey(), innerResultSet);
                                }
                            } else if (operator.equals(">=")) {
                                if (in.getKey().equals(RightColName) && Integer.parseInt(in.getValue()) >= Integer.parseInt(RightValueToUpdate)) {
                                    innerResultSet.put(leftColName,leftvalueToUpdate);
                                    resultSet1.put(i.getKey(), innerResultSet);
                                }
                            } else if (operator.equals("<=")) {
                                if (in.getKey().equals(RightColName) && Integer.parseInt(in.getValue()) <= Integer.parseInt(RightValueToUpdate)) {
                                    innerResultSet.put(leftColName,leftvalueToUpdate);
                                    resultSet1.put(i.getKey(), innerResultSet);
                                }
                            } else if (operator.equals("=")) {
                                if (in.getKey().equals(RightColName) && in.getValue().equals(RightValueToUpdate.replace("\"", ""))) {
                                   innerResultSet.put(leftColName,leftvalueToUpdate);
                                    resultSet1.put(i.getKey(), innerResultSet);
                                }
                            } else if (operator.equals(">")) {
                                if (in.getKey().equals(RightColName) && Integer.parseInt(in.getValue()) > Integer.parseInt(RightValueToUpdate)) {
                                    innerResultSet.put(leftColName,leftvalueToUpdate);
                                    resultSet1.put(i.getKey(), innerResultSet);
                                }
                            } else if (operator.equals("<")) {
                                if (in.getKey().equals(RightColName) && Integer.parseInt(in.getValue()) < Integer.parseInt(RightValueToUpdate)) {
                                    innerResultSet.put(leftColName,leftvalueToUpdate);
                                    resultSet1.put(i.getKey(), innerResultSet);
                                }
                            }
                        }
                        else
                            resultSet1.put(i.getKey(), i.getValue());
                    }
                }
            }
    //        for (Map.Entry<String, HashMap<String, String>> i : resultSet1.entrySet()) {
    //            hashmap.remove(i.getKey());
    //        }
            String ans = "";
            for (Map.Entry<String, HashMap<String, String>> i : hashmap.entrySet()) {
                for (Map.Entry<String, String> j : i.getValue().entrySet()) {
                    ans += j.getValue() + "&&";
                }
                ans = ans.substring(0, ans.length()-2) + "\n";
            }
//            System.out.println(ans);
            FileWriter w = new FileWriter("TableName-" + tbName + ".txt");
            w.write(ans);
            w.close();
            System.out.println("Record Updated successfully");
            return true;
        }
        catch (Exception e){
        return false;
        }

    }

    /**
     * This method is used to parse the select query
     * @param q is the query entered by the user
     */
    public void select(String q) {

        try {
//            boolean valuePresent=true;
            q = q.replace(";","");
            String[] splitedQuery = q.trim().toLowerCase().split(" ");
//            System.out.println(Arrays.toString(splitedQuery));

            if (splitedQuery[0].startsWith("select")) {
//                System.out.println("correct");
                String[] splitedQueryFrom = q.trim().toLowerCase().split("from");
                String[] columns = splitedQueryFrom[0].replace("select", "").split(",");
                for (int i = 0; i < columns.length; i++) {
                    columns[i] = columns[i].trim();
                }
//                System.out.println(Arrays.toString(columns)); we get * or column names here
                String tableName = splitedQueryFrom[1].trim().replace(";", "").split(" ")[0].trim();
//                System.out.println(tableName);
                selectRetrieveal(tableName);
//                getFilteredData(tableName,null,null,null);
                String logicalOperator = new String();
                String[] condition2 = new String[3];
                String[] condition1 = new String[3];



                if(q.contains("where")){
                    if(q.split("where")[1].trim().contains(" and ")){
                        logicalOperator = "and";

                    }
                    else if(q.split("where")[1].trim().contains(" or ")){
                        logicalOperator = "or";
                    }

                    condition1[0] = q.split("where")[1].trim().split(" " + logicalOperator + " ")[0].trim().split("!=|>|<|<=|>=|=")[0].trim();
                    condition1[2] = q.split("where")[1].trim().split(" " + logicalOperator + " ")[0].trim().split("!=|>|<|<=|>=|=")[1].trim();
                    condition1[1] = q.split("where")[1].trim().split(" " + logicalOperator + " ")[0].trim().replace(condition1[0],"").replace(condition1[2],"").trim();
                    if(!logicalOperator.isEmpty()){
                        condition2[0] = q.split("where")[1].trim().split(" " + logicalOperator + " ")[1].trim().split("!=|>|<|<=|>=|=")[0].trim();
                        condition2[2] = q.split("where")[1].trim().split(" " + logicalOperator + " ")[1].trim().split("!=|>|<|<=|>=|=")[1].trim();
                        condition2[1] = q.split("where")[1].trim().split(" " + logicalOperator + " ")[1].trim().replace(condition2[0],"").replace(condition2[2],"").trim();
                    }
                }
                boolean f =  getFilteredData(tableName, columns,logicalOperator, condition1, condition2);
//

            }

        } catch (Exception e) {

        }
    }

    /**
     * This method checks if the table exists or not
     * @param tableName is the name of the table entered by the user in the query
     * @throws FileNotFoundException
     */
    public void selectRetrieveal(String tableName) throws FileNotFoundException {
        try {
            File myObj = new File("TableName-" + tableName + ".txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String dataa = myReader.nextLine();
                if (dataa.contains(delimiter3)) {
                    String[] parts = dataa.split(delimiter3);

                }
            }
        } catch ( Exception e){
            System.out.println("Table Doesnt Exist");

        }


    }

    /**
     * This method is used to get the metadata
     * @param table is the table name entered by the user in the query
     * @return It returns the metadata for the given table if it exists
     */
    public  ArrayList<ArrayList<String>> getMeta(String table){
        try{
            File myObj = new File("metadata.txt");
            Scanner myReader = new Scanner(myObj);
            ArrayList<ArrayList<String>> metaData = new ArrayList<ArrayList<String>>();
            String pk = new String();
            while (myReader.hasNextLine()) {
                String metadataRow = myReader.nextLine();
                if(metadataRow.startsWith(table)){
                    metadataRow = metadataRow.replace(table+"--","");
                    String[] myData = metadataRow.split("#");

                    for (int i =0;i < myData.length;i++){
                        if(i == myData.length-1)
                        {
                            pk = myData[i].split("--")[2];
                            break;
                        }
                        metaData.add(new ArrayList<String>(Arrays.asList(myData[i].split("--"))));
                    }
                    for (int i=0; i < metaData.size(); i++) {
                        if(metaData.get(i).size() == 2)
                            metaData.get(i).add("null");
                    }
//                    System.out.println();
//                    System.out.println();

                }

                }
            metaData.add(new ArrayList<String>(Arrays.asList(pk)));
            return metaData;

            } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * This method is used to get the data from the table
     * @param table is the tablename entered by the user
     * @return It returns the data from the table
     */
    public ArrayList<ArrayList<String>> getTable(String table){
        File myObj = new File("TableName-" + table + ".txt");
        Scanner myReader = null;
        try {
            myReader = new Scanner(myObj);
            ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
            while (myReader.hasNextLine()) {
                String dataa = myReader.nextLine();
                if (dataa.contains(delimiter3)) {
                    ArrayList<String> parts = new ArrayList<String>(Arrays.asList(dataa.split(delimiter3)));
                    data.add(parts);
                }
            }
//            System.out.println(data);
            return  data;
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);

        }

    }

    /**
     * This method is used to retrieve the data for the given conditoon from the table
     * @param tableName is the table name entered by the user in the query
     * @param columns is the column whose value has to be retrieved
     * @param logicalOperator operator that user has specified
     * @param condition1 Condition 1 specified by the user
     * @param condition2 Condition 2 specified by the user
     * @return This method returns a Boolean value indicating success or failure
     */
    public Boolean getFilteredData(String tableName,String[] columns, String logicalOperator, String[] condition1, String[] condition2){
        ArrayList<ArrayList<String>> data = getTable(tableName);
        ArrayList<ArrayList<String>> metaData = getMeta(tableName);
        String pk = metaData.get(metaData.size()-1).get(0);
//        System.out.println(pk);
        HashMap<String, HashMap<String, String>> hashmap = new LinkedHashMap<String, HashMap<String, String>>();
        for (int i=0;i< data.size(); i++){
            HashMap<String, String> tempMap= new HashMap<String, String>();
            String primaryKey=" ";
            for(int j=0;j< data.get(i).size(); j++){
                tempMap.put(metaData.get(j).get(0),data.get(i).get(j));
                if(metaData.get(j).get(0).equals(pk)){
                    primaryKey=data.get(i).get(j);
                }
            }
            hashmap.put(primaryKey,tempMap);
        }
//        System.out.println(metaData);

        Set<String> colNames = new HashSet<String>();
        for(int i=0; i<metaData.size(); i++){
            for(int j=0;j<metaData.get(i).size(); j++){
                colNames.add(metaData.get(i).get(0));
            }
        }
        if(!columns[0].equals("*")){
            for (String col: columns) {
                if(!colNames.contains(col)){
                    System.out.println("Invalid column name '" + col+"' in table \"" + tableName+"\"");
                    return false;
                }

            }
        }
        if(condition1[0] != null){
            if(!colNames.contains(condition1[0])){
                System.out.println("Invalid column name '" + condition1[0]+"' in table \"" + tableName+"\"");
                return false;
            }
        }
        if(condition2[0] != null){
            if(!colNames.contains(condition2[0])){
                System.out.println("Invalid column name '" + condition2[0]+"' in table \"" + tableName+"\"");
                return false;
            }
        }
        String[] colNamesList = colNames.toArray(new String[colNames.size()]);

        HashMap<String, HashMap<String, Object>> outerMap = new HashMap<String, HashMap<String, Object>>();
        HashMap<String, Object> innerMap = new HashMap<String, Object>();
        innerMap.put(pk,data);
        outerMap.put(pk, innerMap);
        HashMap<String, HashMap<String, String>> resultSet1 = new LinkedHashMap<String, HashMap<String, String>>();

            for (Map.Entry<String, HashMap<String, String>> i : hashmap.entrySet()) {
                HashMap<String, String> innerResultSet = i.getValue();
                for (int k = 0; k < colNames.size(); k++) {

                    for (Map.Entry<String, String> in : innerResultSet.entrySet()) {
                        if(condition1[0] != null) {
                            if (condition1[1].equals("!=")) {
                                if (in.getKey().equals(condition1[0]) && !in.getValue().equals(condition1[2].replace("\"", ""))) {
                                    resultSet1.put(i.getKey(), i.getValue());
                                }
                            } else if (condition1[1].equals(">=")) {
                                if (in.getKey().equals(condition1[0]) && Integer.parseInt(in.getValue()) >= Integer.parseInt(condition1[2])) {
                                    resultSet1.put(i.getKey(), i.getValue());
                                }
                            } else if (condition1[1].equals("<=")) {
                                if (in.getKey().equals(condition1[0]) && Integer.parseInt(in.getValue()) <= Integer.parseInt(condition1[2])) {
                                    resultSet1.put(i.getKey(), i.getValue());
                                }
                            } else if (condition1[1].equals("=")) {
                                if (in.getKey().equals(condition1[0]) && in.getValue().equals(condition1[2].replace("\"", ""))) {
                                    resultSet1.put(i.getKey(), i.getValue());
                                }
                            } else if (condition1[1].equals(">")) {
                                if (in.getKey().equals(condition1[0]) && Integer.parseInt(in.getValue()) > Integer.parseInt(condition1[2])) {
                                    resultSet1.put(i.getKey(), i.getValue());
                                }
                            } else if (condition1[1].equals("<")) {
                                if (in.getKey().equals(condition1[0]) && Integer.parseInt(in.getValue()) < Integer.parseInt(condition1[2])) {
                                    resultSet1.put(i.getKey(), i.getValue());
                                }
                            }
                        }
                        else
                            resultSet1.put(i.getKey(), i.getValue());
                    }
                }
            }


        HashMap<String, HashMap<String, String>> resultSet2 = new LinkedHashMap<String, HashMap<String, String>>();
        if(!logicalOperator.isEmpty()) {

            for (Map.Entry<String, HashMap<String, String>> i : hashmap.entrySet()) {
                HashMap<String, String> innerResultSet = i.getValue();
                for (int k = 0; k < colNames.size(); k++) {

                    for (Map.Entry<String, String> in : innerResultSet.entrySet()) {
                        if (condition2[1].equals("!=")) {
                            if (in.getKey().equals(condition2[0]) && !in.getValue().equals(condition2[2].replace("\"",""))) {
                                resultSet2.put(i.getKey(), i.getValue());
                            }
                        } else if (condition2[1].equals(">=")) {
                            if (in.getKey().equals(condition2[0]) && Integer.parseInt(in.getValue()) >= Integer.parseInt(condition2[2])) {
                                resultSet2.put(i.getKey(), i.getValue());
                            }
                        } else if (condition2[1].equals("<=")) {
                            if (in.getKey().equals(condition2[0]) && Integer.parseInt(in.getValue()) <= Integer.parseInt(condition2[2])) {
                                resultSet2.put(i.getKey(), i.getValue());
                            }
                        } else if (condition2[1].equals("=")) {
                            if (in.getKey().equals(condition2[0]) && in.getValue().equals(condition2[2].replace("\"",""))) {
                                resultSet2.put(i.getKey(), i.getValue());
                            }
                        } else if (condition2[1].equals(">")) {
                            if (in.getKey().equals(condition2[0]) && Integer.parseInt(in.getValue()) > Integer.parseInt(condition2[2])) {
                                resultSet2.put(i.getKey(), i.getValue());
                            }
                        } else if (condition2[1].equals("<")) {
                            if (in.getKey().equals(condition2[0]) && Integer.parseInt(in.getValue()) < Integer.parseInt(condition2[2])) {
                                resultSet2.put(i.getKey(), i.getValue());
                            }
                        }
                    }
                }
            }
        }
        HashMap<String, HashMap<String, String>> resultSet = new LinkedHashMap<String, HashMap<String, String>>();
       if(!logicalOperator.isEmpty() && logicalOperator.equals("or")) {
           resultSet.putAll(resultSet1);
           resultSet.putAll(resultSet2);
       }
       else if(!logicalOperator.isEmpty() && logicalOperator.equals("and")) {
           for(Map.Entry<String, HashMap<String, String>> element : resultSet1.entrySet()){
               if(resultSet2.containsKey(element.getKey()))
                   resultSet.put(element.getKey(), element.getValue());
           }
       }
       else {
           resultSet.putAll(resultSet1);
       }
        HashMap<String, HashMap<String, String>> resultSetFinal = new LinkedHashMap<String, HashMap<String, String>>();
        if(columns[0].equals("*")){
            resultSetFinal.putAll(resultSet);
        }
        else {
            ArrayList<String> tempCol = new ArrayList<String>(Arrays.asList(columns));
            for (Map.Entry<String, HashMap<String, String>> i : resultSet.entrySet()) {
                HashMap<String, String> innerResultSet = i.getValue();
                LinkedHashMap<String ,String> temp = new LinkedHashMap<String,String>();
              //  for (int k = 0; k < colNames.size(); k++) {

                    for (Map.Entry<String, String> in : innerResultSet.entrySet()) {
                        if (tempCol.contains(in.getKey())) {
                            //in.getKey()
                            //innerResultSet.remove(in.getKey());
                            temp.put(in.getKey(), in.getValue());
                        }

                    }

                //}
                resultSetFinal.put(i.getKey(), temp);
            }
        }
        if(columns[0].equals("*"))
            columns = colNamesList;
        for(int i=0; i<columns.length;i++){
            System.out.print(columns[i]+"\t\t|\t");
        }
        System.out.println();
        System.out.println("------------".repeat(columns.length));
        for(HashMap.Entry<String, HashMap<String,String>> element : resultSetFinal.entrySet()){
            HashMap<String, String> tempMap= new HashMap<String, String>();
            tempMap = element.getValue();
            for(HashMap.Entry<String, String> innerElement : tempMap.entrySet()){
                System.out.print(innerElement.getValue()+"\t\t|\t");
            }
            System.out.println();
        }

        return true;

    }

}


