import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Scanner;

public class LoginSignupManagement {

    private static String delimiter = ",";
    private static String delimiter1 = "#";

    /**
     * This method is used to encrypt the password
     * @param s is the password entered by the user
     * @return returns encrypted password
     * @throws Exception
     */
    public String hashPassword(String s) throws Exception {
        /**
         * Code adapted from the article
         * https://infosecscout.com/decrypt-md5-in-java/
         */
        MessageDigest m= MessageDigest.getInstance("MD5");
        m.update(s.getBytes(),0,s.length());
        return new BigInteger(1,m.digest()).toString(16);
    }

    /**
     * This method is used to create a new account for the user
     */
    public void signUp(){
        try{
            Scanner input = new Scanner(System.in);
            System.out.println("Enter Username");
            String uname = input.nextLine();
            File myObj = new File("login.txt");
            Scanner myReader = new Scanner(myObj);
            boolean valuePresent=true;
            while (myReader.hasNextLine()) {
                String dataa = myReader.nextLine();
//                System.out.println(">>>" +dataa);
                String[] dataArrr = dataa.split(delimiter1);
//                System.out.println(Arrays.toString(dataArrr));
                for(String eachElement:dataArrr){
                    String eachRow[]=eachElement.split(",");
                    if(uname.equals(eachRow[0])){
                        valuePresent=false;
                        System.out.println("Username already present || Please try something different");
                        break;
                    }
                }
                if(valuePresent){
                    System.out.println("Enter Password");
                    String pass = input.nextLine();
                    String hashpass = hashPassword(pass);
                    System.out.println("Please enter your security question");
                    String securityQs = input.nextLine();
                    System.out.println("Please enter your answer");
                    String securityAns = input.nextLine();
                    String data = uname + delimiter  + hashpass + delimiter+ securityQs + delimiter + securityAns +  delimiter1;
                    FileWriter myWriter = new FileWriter("Login.txt", true);
                    myWriter.write(data);
                    myWriter.close();
                    System.out.println("Your account has been successfully created");
                }
            }
        }catch (IOException e){
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * This method is used to log the user in.
     */
    public void login() {
        LogManagement obj = new LogManagement();
        QueryValidation qu = new QueryValidation();
        try {
            Scanner input = new Scanner(System.in);


            System.out.println("please enter the Username");
            String username = input.nextLine();
            System.out.println("please enter the password");
            String password = input.nextLine();

            File myObj = new File("login.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
               String[] dataArr = data.split(delimiter1);
                String[] row = null;
                for(String eachElement:dataArr) {
                    String eachRow[] = eachElement.split(",");

                    if (username.equals(eachRow[0]) && hashPassword(password).equals(eachRow[1])){
                        row = eachRow;
                        System.out.println("Correct Password");
                        System.out.println(eachRow[2]);
                        break;
                    }
                }
                if(row!=null && row.length > 0){
                    String securityAns = input.nextLine();
                    if (username.equals(row[0]) && securityAns.equals(row[3])) {
                        System.out.println("logged in successfully");
                        qu.queryType();
                        obj.logger(username,"Logged in","Query Initiated");
                        break;
                    }else{
                        System.out.println("Incorrect Security Answer || Please try again");
                    }
                }else{
                        System.out.println("Invalid Credentials");
                }
            }
            myReader.close();
        }
        catch(Exception e){
                System.out.println(e.getMessage());
            }

        }

    }
