public class SquareMapping {


    public static int[][] returnGradient(int[][] imagePix, int width, int height) {

        int[][] newImg = new int[width][height];

        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                double dvX = (imagePix[x + 1][y] - imagePix[x - 1][y]) / 2;
                double dvY = (imagePix[x][y + 1] - imagePix[x][y - 1]) / 2;
                double theta = Math.atan(dvY / dvX);
                theta = Math.toDegrees(theta);

                newImg[x][y] = (int) theta;


            }
        }

        return newImg;

    }


    public static void generateResponseMap(int[][] dogImage, int[][] hough, int[][] peaks, int width, int height, int squareLength) {
        //responseMap[0] refers to the orientation of the potential square at the pixel
        //responseMap[1] refers to the central position of the potential square at the pixel, this will increment at the coordinate if a centroid is calculated
        //responseMap[2] refers to the translation of the potential square at the pixel;

        int[][][] responseMap = new int[3][width][height];

        Image imageTemp = new Image();
        imageTemp.height = height;
        imageTemp.width = width;
        int[][] corners = new int[4][2];
        int[][] temp = new int[width][height];
        ImagePPM imageCol = new ImagePPM();
        imageCol.width = width;
        imageCol.height = height;
        imageCol.pixels[0] = dogImage;
        imageCol.pixels[1] = dogImage;
        imageCol.pixels[2] = dogImage;


        responseMap[0] = returnGradient(dogImage, width, height);
        for (int peak = 0; peak < 500; peak++) {
            for (int peak2 = 0; peak2 < 250-peak; peak2++) {
                imageCol.pixels[1] = HoughGeneration.addToArray(temp, height, width, peaks[0][peak], peaks[1][peak], height, 180);
                }
        }
        imageCol.WritePPM("lines.ppm");
    }
}








