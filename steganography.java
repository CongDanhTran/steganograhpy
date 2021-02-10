import greenfoot.*; 
import java.io.*;       // for file input and output
import javax.swing.*;   // the GUI items - modal dialog boxes, etc.
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.lang.Object;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.Color;
/**
  * @version 3.0
  * @author Pratheek, Andy, Harry
  * 
  * 
  */
public class steganography extends World{
   
        //bytes array for input file 
        byte[] byteArrayHidden=null;
        byte[] byteArrayVisible = null;
        byte[] combined = null;
   
   public steganography()
    {    
        super(1400,800, 1);
        
        getBackground().setColor(java.awt.Color.WHITE);
        getBackground().fill();
         
       /* Picture pict = new MyPicture("images/green-forest.png");
        this.addObject(pict,700,400);*/
    }
    

   /**
     * Hide the image using 2 parameter
     * 
     * @param id the first key to encrypt the file
     * @param password the second key to encrypt the file
     */ 
    public void hideImage(int id, int password, int reduceBitImage){
       
        try{
           
           //encrypt hidden file or picture
           encryptImage(id,password);
           
           //link hidden file to the end of the picure 
           hideFileinPicture(reduceBitImage);
        }catch(NullPointerException er){
            System.out.print("\nNo file destination");
            return;
        
        }catch(Exception e){
            System.out.print("\nError");
            return;
        }
        
   }
   
   
   /**
     * Restore the image by using 3 different keys
     *
     * @param id
     * @param password 
     * @param useless
     */ 
   public void restoreImage(int id,int password, int useless){
       
       try{
           //take encrypted file out of the picture
           restoreEncryptedFileInImage(useless);
           
           //decrypt it with id and password
           decryptImage(id,password);
       }catch(NullPointerException er){
           System.out.print("\nNo file destination");
           return;
       }catch(Exception e){
           System.out.print("\nError");
           return;
       }
   }
   
   /**
     * Encrypted file or image by using 2 parameter 
     * 
     * @param id the first key to encrypt the file
     * @param password the second key to encrypt the file
     * @throws NullPointerException 
     */ 
   private void encryptImage(int id,int password) throws Exception,NullPointerException {   
        
       // get path hidden files
        JFileChooser chooser = new JFileChooser(".");
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)return;
        Path hiddenImagePath = Paths.get(chooser.getSelectedFile().getAbsolutePath());
       
        //check exception + covert files to byte array
        try{
          
            byteArrayHidden= Files.readAllBytes(hiddenImagePath); 
           
        }catch (Exception e){
            
            System.out.print("\nfile can't be read") ;
            return;
        }

        System.out.print("\n--------------------");
        System.out.print("\nId for decryption :" + id); //1st key
        System.out.print("\nPassword for decryption :" + password); //2nd key
        
        //encryption 
        for(int i=0; i<byteArrayHidden.length;i++){
               
             byteArrayHidden[i]=(byte)(~(~byteArrayHidden[i] ^ id)^~password); //Using XOR and NOT operation
        }
     
        System.out.println("\nEncrypt Done");
    }
    
   /**
    * Hiding the picture in a file using a try-catch block and byte array
    * 
    * @throws Exception
    * @exception IOException
    * 
    */
    private void hideFileinPicture(int reduceBitImage) throws Exception{
       
        // Choose visible picture + get path
        System.out.println("\n****Please choose visible picture*****");
        JFileChooser chooser = new JFileChooser(".");
        if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) throw new NullPointerException();
        Path pathOrginalimage = Paths.get(chooser.getSelectedFile().getAbsolutePath());
       
       
        // covert picture and files into bytes array
        try{
    
           byteArrayVisible = Files.readAllBytes(pathOrginalimage);
        }catch (IOException e){
            
           System.out.print("\nFile can't be read");
        }
        
        //reduce certain bit of the image - no change bit to 7 bit
        if(reduceBitImage>0&&reduceBitImage<8){
         byteArrayVisible =reduceBitImage(reduceBitImage,byteArrayVisible);
        }else{System.out.print("\nWrong input Bit number. Default: no changes");}
         
        
        System.out.print("\nUseless bytes: " + byteArrayVisible.length) ; // 3rd Key
      
        //merge two bytes array
        combined = new byte[byteArrayVisible.length +  byteArrayHidden.length];
        System.arraycopy(byteArrayVisible,0,combined,0, byteArrayVisible.length);
        System.arraycopy(byteArrayHidden,0,combined,byteArrayVisible.length,byteArrayHidden.length);

        // chose decrypted file destination
        JFileChooser chooser1 = new JFileChooser(".");
        if (chooser1.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) throw new NullPointerException();
       

        // write merge array into file 
        try{
           
            FileOutputStream fosEncrypted = new FileOutputStream(chooser1.getSelectedFile().getAbsolutePath());
            fosEncrypted.write(combined);
            fosEncrypted.close();
            System.out.println("\n******Hide Done******");
            System.out.println("\nNew File name: " + chooser1.getSelectedFile().getName());
       }catch(Exception e){
            System.out.print("\nError");
       }
   }

   
   /**
    * Restoring the Encrypted File in an image
    * It converts the file into byte array and then takes separate the byte array
    * 
    * @param id
    * @param password
    * @param byte array
    * @param uselessByte
    * @throws Exception
    * @exception IOException
    * @return file cannot be read
    * @exception OutOfMemory
    * @return run out heap memory
    * @exception NegativeArraySizeException
    * @return wrong key
    * 
    */   
   private void restoreEncryptedFileInImage(int uselessByte) throws Exception{
      
       // choose encrypted file to decrypt + get path
       JFileChooser chooser = new JFileChooser(".");
       if (chooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) throw new NullPointerException();
       Path pathHiddenimage = Paths.get(chooser.getSelectedFile().getAbsolutePath());
     

       // convert file to bytes array
       try{  
           // declare the length of the byte array + catch Out of heap memory exception
           byteArrayVisible= new byte[uselessByte];
           combined = Files.readAllBytes(pathHiddenimage);
       }catch (IOException e){
           System.out.print("\nFile can't be read!");
           return;
       }catch (OutOfMemoryError e){
           System.out.print("\nRun out heap memory");
           return;
       }
        
       //get hidden array out of the picture + check wrong file
       try{
            byteArrayHidden = new byte[combined.length - byteArrayVisible.length];
            
            if(byteArrayHidden.length >0) {
                System.arraycopy(combined,byteArrayVisible.length,byteArrayHidden,0, byteArrayHidden.length);
            } 
       }catch (NegativeArraySizeException e){ 
           
            System.out.print("\nWrong Key");
            return;
       }
       catch (Exception e){   
            
            throw new Exception();
       }
       System.out.println("\nRestore Done");
   }
 

   /**
    * Decrypting the Image from the hidden picture
    * 
    * @param id
    * @param password
    * @exception NullPointerException
    */
  private void decryptImage(int id,int password){
       
        // decryption
        for(int i=0; i<byteArrayHidden.length;i++){
           byteArrayHidden[i]=(byte)(~(~byteArrayHidden[i] ^ id)^~password); // Excute XOR and Not operation again to decrypt the file
        }
        
        
        // chose decrypted file destination
        System.out.println("\n****Please choose destination for new picture*****");
        JFileChooser chooser = new JFileChooser(".");
        if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) throw new NullPointerException();
        
        
        //write hidden file 
        try{
          FileOutputStream fileOuputStream = new FileOutputStream(chooser.getSelectedFile().getAbsolutePath());
          fileOuputStream.write(byteArrayHidden);
          fileOuputStream.close();
          System.out.println("\n******Decrypt Done******");
          System.out.println("\nRestored File name: " + chooser.getSelectedFile().getName());
        }catch(Exception e){
          e.printStackTrace();
          System.out.print("\nError");
        }
   }
   
    /**
    * This method will reduce a certain bit of the image then return a new byte array.
    * 
    * @param bit
    * @param inputbyte
    */
   private byte[] reduceBitImage(int bit,byte[] inputbyte)
        {
        BufferedImage img = null;
        ByteArrayInputStream bais = new ByteArrayInputStream(inputbyte);
        int bitshift;
    
        try {
            img = ImageIO.read(bais);
        } catch (IOException e) {
            e.printStackTrace();
           
        }
        
         int pixels[][] = new int[img.getWidth()][img.getHeight()];
        for (int x = 0; x < pixels.length; x++) {
            for (int y = 0; y < pixels[x].length; y++) {
                pixels[x][y] = img.getRGB(x, y);
            }
        }

        // gets the height and width of the displayed carrier image
    
        int picWidth  = img.getWidth();
        int picHeight = img.getHeight();
    
        // declare 2D arrays to hold the red, green and blue components of the carrier image pixels
    
        int[][] redChannel   = new int [picWidth][picHeight];
        int[][] greenChannel = new int [picWidth][picHeight];
        int[][] blueChannel  = new int [picWidth][picHeight];
        int[][] alphaChannel = new int [picWidth][picHeight];
        
  
        BufferedImage newImg = new BufferedImage(picWidth, picHeight , BufferedImage.TYPE_INT_RGB);
        
        switch(bit)
        {

            case 1:  bitshift=2;
                     break;
            case 2:  bitshift=4;
                     break;
            case 3:  bitshift=8;
                     break;
            case 4:  bitshift=16;
                     break;
            case 5:  bitshift=32;
                     break;
            case 6:  bitshift=64;
                     break;
            case 7:  bitshift=128;
                     break;
            default: bitshift=1;
                     break;
        }
    
        // loop through the carrier image pixels and shift image into higher bits
    
        for (int i = 0; i < picWidth; i++)
            for (int j = 0; j < picHeight; j++)
            { 
               
                int rgb = img.getRGB(i, j);
                
                alphaChannel[i][j] = (rgb >> 24) & 0xFF;
                redChannel[i][j]   =(rgb >> 16) & 0xFF;
                greenChannel[i][j] =(rgb >>8 ) & 0xFF;
                blueChannel[i][j]  = (rgb)  & 0xFF;
                
         
                
                // scale the intensities of the carrier image to fit in the highest 4 bits
           
                redChannel[i][j]   = Math.max(((redChannel[i][j]   / bitshift) * bitshift), 0);
                greenChannel[i][j] = Math.max(((greenChannel[i][j] / bitshift) * bitshift), 0);
                blueChannel[i][j]  = Math.max(((blueChannel[i][j]  / bitshift) * bitshift), 0);
                alphaChannel[i][j] = Math.max(((alphaChannel[i][j]  / bitshift) * bitshift), 0);
                
                Color col = new Color(redChannel[i][j], greenChannel[i][j], blueChannel[i][j],alphaChannel[i][j]);
                newImg.setRGB(i, j, col.getRGB());
                
            }
            
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
          
            ImageIO.write(newImg, "png",baos);
         
        } catch (IOException e) {
            e.printStackTrace();
            
        }
        
        byte[] bytes = baos.toByteArray();
        return bytes;
  }   
}