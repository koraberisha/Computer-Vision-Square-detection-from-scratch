import java.util.*;



public class HoughGeneration {

	public static int[][] houghTransformTwo(Image inputData, int thetaAxisSize, int rAxisSize, int minContrast)
	{
		int width = inputData.width;
	    int height = inputData.height;
	    double maxDist = Math.sqrt(width*width+height*height);
	    int[][] outputData = new int[thetaAxisSize][rAxisSize];
	    int[][] thetaTable = new int[width][height];

	    //thetaTable = returnGrayLevelMoment(inputData,outputData,width,height);
	    thetaTable = SquareMapping.returnGradient(inputData.pixels,width,height);

		for (int i =0; i<height; i++)
			for (int j =0; j<width; j++)
			{
				{
				if (contrast(inputData.pixels, j,i, minContrast,width,height)) {
					for (int t =0; t<thetaAxisSize ; t++)
				{
					double theta=Math.PI*( ( double ) t / (double ) thetaAxisSize ) ;
					double r=(j)*Math.cos ( theta )+(i)*Math.sin( theta );


					double R=(int ) ( ( r / maxDist )*rAxisSize) ;


					if (j>=0 && i>=0 )
						{
						outputData[(int) Math.abs( t )][ (int) Math.abs(R) ] += 1;
						}
			}
			}
				}
			}
			
		
		for (int x=0;x<width;x++) {
			for (int y=0;y<height;y++) {

					double theta=Math.PI*( ( double ) thetaTable[x][y] / (double ) thetaAxisSize ) ;
					double p = x*Math.cos(thetaTable[x][y]) - y*Math.sin(thetaTable[x][y]);
					int newP = (int) Math.abs( p );
					if (newP<height && Math.abs(thetaTable[x][y]) > 0)
						if (outputData[Math.abs(thetaTable[x][y])][newP] != 0){

							outputData[Math.abs(thetaTable[x][y])][newP] += 1;
						}


			}
		}
		
		int[][] outputData2 = new int[width][height];
		return outputData;
		
	}
	public static int[][] addToArray(int[][] newImage, int height, int width, int currentX, int currentY, int rBins, int tBins) {
		double rMax = Math.sqrt(width * width + height * height);
		double r = rMax * ((double) currentY / (double) rBins);
		double t = (Math.PI) * ((double) currentX / (double) tBins);
		for (int x = -height - 60; x < ((height - 1) + 60); x += 1) {

			double y = Math.sin(-t) * x + Math.cos(-t) * r;
			double m = Math.cos(-t) * x - Math.sin(-t) * r;

			int y1 = (int) y;
			int x1 = (int) m;



			if (x1 >= 0 && x1 < height && y1 >= 0 && y1 < width) {

				newImage[y1][(int) x1] = 250;

			}

		}
		return newImage;
	}

	public static int[][] visualisingPeaks2(int[][] H,int rBins, int tBins,int width, int height,double f1) {
		int neighbourhoodSize = 9;
		int doubleHeight = height;
		int threshold = (int) (f1*returnHighest(H,tBins,rBins));
		int[][] newImage = new int[width][height];
		int[][] peaks = new int[2][1000];
		int count = 0;
		for (int t = 0; t < tBins; t++) {
			loop:
			for (int r = neighbourhoodSize; r < doubleHeight - neighbourhoodSize; r++) {
				if (H[t][r] > threshold) {
					int peak = H[t][r];
					for (int dx = -neighbourhoodSize; dx <= neighbourhoodSize; dx++) {
						for (int dy = -neighbourhoodSize; dy <= neighbourhoodSize; dy++) {
							int dt = t + dx;
							int dr = r + dy;
							if (dt < 0) dt = dt + tBins;
							else if (dt >= tBins) dt = dt - tBins;
							if (H[dt][dr] > peak) {
								continue loop;
							}
						}
					}

					// calculate the true value of theta
					double theta = t * Math.PI/tBins;
					peaks[0][count] = t;
					peaks[1][count] = Math.abs(r);
					//System.out.println(t);
					//System.out.println(r);
					newImage = addToArray(newImage,height,width,t ,r,rBins,tBins);
					count++;
				}
			}
		}
		Image newImg = new Image();
		newImg.pixels = newImage;
		newImg.width = width;
		newImg.height = height;
		newImg.WritePGM("lines.pgm");
		return peaks;
	}

	public static int returnHighest(int[][] input,int width,int height) {

		int highest = 0;

			int tempHighest = 1;
			for (int y=0;y<width;y++) {
				for (int x=0;x<height;x++) {
					if (tempHighest<input[y][x]) {
						tempHighest = input[y][x];
						input[y][x] = 0;
					}
				}
			}
			highest = tempHighest;




		return highest;

	}

	public static boolean contrast(int[][] image1, int x,int y, int minContrast, int width, int height)
    {
		width -= 8;
		height -= 8;
      int centerValue = image1[x][y];
      for (int i = 8; i >= 0; i--)
      {
        if (i == 4)
          continue;
        int newx = x + (i % 3) - 1;
        int newy = y + (i / 3) - 1;
        if ((newx < 0) || (newx >= width) || (newy < 0) || (newy >= height))
          continue;
        if (Math.abs(image1[newx][newy] - centerValue) >= minContrast)
          return true;
      }
      return false;
    }
	public static double moment(int value,int x,int y, int xPow, int yPow) { return (Math.pow(x,xPow))+(Math.pow(y,yPow))+value; }

	public static double[][] returnGrayLevelMoment(Image image, int[][] outputData, int width, int height) {
		double[][] orientationArray = new double[width][height];
		for (int i=10;i<width-10;i++) {
			for (int n=10;n<height-10;n++) {
				int newVal = 0;
				int m00 = 0;
				int m10 = 0;
				int m01 = 0;
				int m20 = 0;
				int m11 = 0;
				int m02 = 0;
				double u20 = 0;
				double u11 = 0;
				double u02 = 0;
				double theta = 0;
				double[] centroid = new double[2];
				
				for (int sx=-3;sx<=3;sx++) {
					for (int sy=-3;sy<3;sy++) {
						newVal = (image.pixels[i+sx][n+sy]);
						
						m00 += newVal;
						m10 += moment(newVal,i+sx,n+sy, 1, 0);
						m01 += moment(newVal,i+sx,n+sy, 0, 1);
						m20 += moment(newVal,i+sx,n+sy, 2, 0);
						m11 += moment(newVal,i+sx,n+sy, 1, 1);
						m02 += moment(newVal,i+sx,n+sy, 0, 2);
						
					}
				}
				if (m00!=0) {
					centroid[0] = m10/m00;
					centroid[1] = m01/m00;
					u20 = ((m20/m00) - Math.pow(centroid[0], 2)) ;
					u11 = ((m11/m00) - (centroid[0]*centroid[1]) ) ;
					u02 = ((m02/m00) - Math.pow(centroid[1], 2)) ;
					double dim = 0;

					
					
					theta = 0.5*Math.atan((2*u11)/u20-u02); 
						//theta = Math.abstheta+sx;
					int degree = Math.abs((int) ((theta*180)/Math.PI));
					dim = n*Math.cos(theta) - i*Math.sin(theta);
					dim = Math.abs(dim);
						

					//outputData[(int) dim][i+sx] += 10*(image.pixels[n][i*(image.height/thetaSize)]);
					orientationArray[i][n] =theta;

				
					

							
					}
					

				}

			}
		
		
		return orientationArray;
		
	}


	
}
