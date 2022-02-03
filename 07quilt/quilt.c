#include "graphics.h"
#include <stdlib.h>

int main()
{   
    int gd = DETECT, gm;
    const int HEIGHT = 400, WIDTH = 400;
    float 

    initgraph(&gd, &gm, "C:\\TC\\BGI");

    //scanf("%f %f", str, &i);
    
    for ( radius = 25; radius <= 125 ; radius = radius + 20)
        circle(x, y, radius);
        
    getch();
    closegraph();
    return 0;
}