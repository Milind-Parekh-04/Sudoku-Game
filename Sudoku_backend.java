class Sudoku_backend{
    public static boolean rowcheck(int r , int val , int[][] sud){
        for(int i=0;i<sud[r].length;i++){
            if(val==sud[r][i]){
                return false;
            }
        }
        return true;
    }

    public static boolean colcheck(int c , int val , int[][] sud){
        int[] colm = new int[sud.length];
        for(int i=0;i<sud.length;i++){
            colm[i] = sud[i][c];
        }
        for(int i=0;i<colm.length;i++){
            if(val==colm[i]){
                return false;
            }
        }
        return true;
    }

    public static boolean boxcheck(int r , int c , int val , int[][] sud){
        int box_start_row = (Math.floorDiv(r, 3))*3;
        int box_start_col = (Math.floorDiv(c, 3))*3;
        for(int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                if(sud[box_start_row+i][box_start_col+j]==val){
                    return false;
                }
            }
        }
        return true;
    }

    public static int[] find_empty(int[][] sud){
        int[] ans = {25 , 25};
        for(int i=0;i<sud.length;i++){
            for(int j=0;j<sud[i].length;j++){
                if(sud[i][j]==0){
                    int[] anss = {i,j};
                    return anss;
                }
            }
        }
        return ans;
    }

    public static boolean solve_sudoku(int[][] sudok){
        int[] empty_finder = find_empty(sudok);
        int row = empty_finder[0];
        int col = empty_finder[1];
        
        if(row==25){
            return true;
        }
        for(int i=1;i<10;i++){
            if(rowcheck(row, i, sudok)==true && colcheck(col, i, sudok)==true && boxcheck(row, col, i, sudok)==true ){
                sudok[row][col] = i;
                if(solve_sudoku(sudok)==true){
                    return true;
                }
                sudok[row][col] = 0;
            }
        }
        return false;
    }

    public static int[][] main(int[][] sudoku)
    {
        // int sudoku = new int[9][9];
        int[][] sudoku_copy = sudoku;
        int[][] nonzero = new int[81][2];
        int flag = 0;
        for(int i=0;i<sudoku_copy.length;i++){
            for(int j=0;j<sudoku_copy[i].length;j++){
                if(sudoku_copy[i][j]!=0){
                    nonzero[flag][0] = i;
                    nonzero[flag][1] = j;
                    flag = flag + 1;
                }
            }
        }
        // for(int i=0;i<sudoku.length;i++){
        //     for(int j=0;j<sudoku[i].length;j++){
        //         System.out.print(sudoku[i][j]+" ");
        //     }
        //     System.err.println();
        // }
        solve_sudoku(sudoku_copy);
        return sudoku_copy;
        // ArrayDisplay Showoutput = new ArrayDisplay();
        // Showoutput.ArrayDispla(sudoku,nonzero);
    }
}