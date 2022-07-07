
public class Sobel {


	public static int[][] generateCacheX() {
		
		int[][] xCache;
		xCache = new int[3][3];
        xCache[0][0] = -1;
        xCache[0][1] = 0;
        xCache[0][2] = 1;
        xCache[1][0] = -2;
        xCache[1][1] = 0;
        xCache[1][2] = 2;
        xCache[2][0] = -1;
        xCache[2][1] = 0;
        xCache[2][2] = 1;
        return xCache;
	}
	
	public static int[][] generateCacheY() {
		int[][] yCache;
		yCache = new int[3][3];
        yCache[0][0] = -1;
        yCache[0][1] = -2;
        yCache[0][2] = -1;
        yCache[1][0] = 0;
        yCache[1][1] = 0;
        yCache[1][2] = 0;
        yCache[2][0] = 1;
        yCache[2][1] = 2;
        yCache[2][2] = 1;
        return yCache;
	}
	
	public static int[][] sobelCalculation(Image image) {
		int[][] yCache = generateCacheY();
		int[][] xCache = generateCacheX();
		double g;
		double[] sum = new double[2];
		sum[0] = 0.0;
		sum[1] = 0.0;
		for (int y = 3; y < image.height-3; y++) {
            for (int x = 3; x < image.width-3; x++) {
            	sum[0] = 0.0;
        		sum[1] = 0.0;
        		for (int p = -1; p <= 1; p++) {
                    for (int q = -1; q <= 1; q++) {
                    	sum[0] = sum[0]+image.pixels[x + p + 1][y + q + 1] * xCache[q + 1][p + 1];
                    	sum[1] = sum[1]+image.pixels[x + p + 1][y + q + 1] * yCache[q + 1][p + 1];
        		}
        		}
        		g = Math.sqrt(Math.pow(sum[0], 2) + Math.pow(sum[1], 2)) / 4;
        		if(g > 255) {
        			g = 255;
        			}
        		else if (g < 0) {
        			g = 0;
        			}
        		image.pixels[x][y] = (int)g;
            }
		}
		return image.pixels;
		
	}
	
}
