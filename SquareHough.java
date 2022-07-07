import java.math.*;
import java.io.*;
import java.util.*;

public class SquareHough {
	
	public static int[][] subtractMatricies(int[][] a, int[][] b) {
		
		int rows = a.length;  
        int cols = a[0].length;  
 
        int diff[][] = new int[rows][cols];  
        for(int i = 0; i < rows; i++){  
            for(int j = 0; j < cols; j++){  
                diff[i][j] = a[i][j] - b[i][j];  
            }  
        }  
        return diff;
	}

	public static double[][] differenceOfGaussian(double sig1) {
		double x = 0;
		double y = 0;
		double filter_size = 0.0;
		if (sig1%2==0) {
			filter_size = 2*3*sig1;
			filter_size += 1;
		}
		else {
			filter_size = 2*3*sig1;
		}
		
		
		double width = filter_size/2;
		double height = filter_size/2;

		int sum = 0;
		double[][] temp = new double[(int) filter_size][(int) filter_size];
		for (int n=(int) -width;n<=width-1;n++) {
			for (int i=(int) -height;i<=height-1;i++) {
				x = n;
				y = i;
				double x1 = 2*Math.PI*(sig1*sig1);
				double x2 = Math.exp((-(x*x+y*y)+(2*sig1*sig1)));
				temp[(int) (n+width)][(int) (i+height)] = (1/x1)*x2;
				sum += (int) (temp[(int) (n+width)][(int) (i+height)]);
			}
		}

		return temp;

	}	
	
	public static double sumMatrix(double[][] kernel,int filterSize) {
		double perTotal = 0;
		int numCols = filterSize;
		int numRows = filterSize;
		for (int c = 0; c < numCols; c++) {
			for (int r = 0; r < numRows; r++) {
				perTotal += kernel[c][r];
			}
		}

		return perTotal;

	}

	public static int[][] convolution2D(Image image,double[][] kernel, double sig) {
		int[][] pixels = image.pixels;
		int inputWidth = (int) image.width;
		int inputHeight = (int) image.height;
		int kernelWidth = (int) (2*3*sig)/2;
		int kernelHeight = (int) (2*3*sig)/2;;
		int offset = 0;
		
		
		if (sig%2==0 ) {
			offset = -1;
		}
		
		int filterSize = kernelWidth+(offset*2)/2;
		double divisor = sumMatrix(kernel,(int) (2*3*sig)+offset);
		
		int[][] outputMatrix = new int[inputWidth][inputHeight];
		int outputValue = 0;
		for (int i = filterSize; i < (inputWidth)-filterSize; i++)
	    {
	      for (int j = filterSize; j < (inputHeight)-filterSize; j++)
	      {
	        double newValue = 0.0;

	        for (int kw = -filterSize; kw < filterSize; kw++) {
	          for (int kh = -filterSize; kh < filterSize; kh++) {
	        	  

	        	  newValue += kernel[kw+filterSize][kh+filterSize]*pixels[i+kw][j+kh];
	          }
	        }
	        outputMatrix[i][j] = (int) (newValue/divisor);
	        //if ((int) (newValue/divisor) >220) {
	        //	outputMatrix[i][j] = 0;
	        //}
	        //else {
	        //	outputMatrix[i][j] = (int) (newValue/divisor);
	        //	
	        //}
	        
	        	  
	      }
	    }
		return outputMatrix;

	}
	
	public static int[][] returnNeighborhood(int n, Image image, int x, int y) {
		int offset = (int) n/2;
		int[][] outputNeigh = new int[offset][offset];
		offset = (int) offset/2;
		for (int i=1-offset;i<=offset-1;i++) {
        	for (int p=1-offset;p<offset-1;p++) {
        		outputNeigh[i+offset][p+offset] = image.pixels[x+i][y+i];
        	}
		}		
		return outputNeigh; 
	}

	public static int[][] thresholdQuick(int[][] input, int thresh,int width, int height) {
		for (int i=0;i<width;i++) {
			for (int n=0;n<height;n++) {
				if (input[i][n] >=thresh ) {
					input[i][n] = 255;
				}
				else {
					input[i][n] = 0;
				}
			}
		}
		return input;
	}
	
	
	public static void main(String[] args)
	throws java.io.IOException
	{	    
	        String fileNameIn =  args[0];
			String SquareSize = args[1];
			String deltaTheta = args[2];
			String f1 = args[3];
			String f2 = args[4];
			String f3 = args[5];
			String dogOrSobel = args[6];

			int squareSizeInt = Integer.parseInt(SquareSize);
			int deltaThetaSize = Integer.parseInt(deltaTheta);
			double f1Int = Double.parseDouble(f1);



			String fileNameOut = "DoG.pgm";
	        String fileNameOut3 = "accumulator.pgm";

	        Image image = new Image();
	        Image image2 = image;
	        Image image3 = image;
	        //ImagePPM image2 = new ImagePPM();
	        
	        image.ReadPGM(fileNameIn);	
	        double[] sig = {2,1};

			if (dogOrSobel.compareTo("E")==0) {
				image3.pixels = Sobel.sobelCalculation(image);
			}

	        double[][] gauss = differenceOfGaussian(sig[0]);
	        int[][] madting = convolution2D(image3, gauss, sig[0]);

	        double[][] gauss2 = differenceOfGaussian(sig[1]);
	        int[][] madting2 = convolution2D(image3, gauss2, sig[1]);


    		image3.pixels =  subtractMatricies(madting,madting2);
    		//image3.pixels = thresholdQuick(image3.pixels,5,image.width,image.height);
    		image3.pixels = Threshold.calculateIdeal(image3);

			int[][] newting = image.pixels;


    		image3.WritePGM(fileNameOut);


    		int maxR = (int)Math.ceil(Math.hypot(image3.width, image3.height));
    		int maxT;
			int[][] peaks = new int[2][50];
    		maxR = image.height;
    		maxT = 180;
    		image2.pixels = HoughGeneration.houghTransformTwo(image3,maxT,maxR,245);
			peaks = HoughGeneration.visualisingPeaks2(image2.pixels, image.height, 180,image.width, image.height,f1Int);
			SquareMapping.generateResponseMap(newting,image2.pixels,peaks,image.width,image.height,squareSizeInt);
    		image2.width = maxT;
    		image2.height = maxR;
    		image2.WritePGM(fileNameOut3);



}
}


