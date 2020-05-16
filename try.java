


#include <stdio.h> 
#include <iostream> //system library


#include <opencv2/core/core.hpp> //include all the basic data types from opencv
#include <opencv2/highgui/highgui.hpp>//allow you to load image from computer or display image onto the screen
#include <opencv2/imgproc/imgproc.hpp> //provide functions to process an image

using namespace cv; //tell the compiler most of the data types involved are from OpenCV
using namespace std; //C++ library


Mat canvas;
Point p1, p2, p3, p4;
vector<Point> pl;
//vector <line > lines;
int counter = 0; //record user's click times
int idx = -1;
int r = 255,b = 0,g = 0;
int linectrl = 0;
void removePoints()
{
		for (int i = 0;i < pl.size();i++)
		{
			circle(canvas, pl[i], 1, Scalar(255,255,255), 2);
		}

}
void splines(Point p1, Point p2, Point p3, Point p4,int lc)
{

	double M_data[4][4] = { { -1, 3, -3, 1 }, { 3, -6, 3, 0 }, { -3, 3, 0, 0 }, { 1, 0, 0, 0 } };
	Mat M = Mat(4, 4, CV_64FC1, M_data);
	double C_data[4][2] = {
	{(double)p1.x,(double)p1.y},
	{(double)p2.x,(double)p2.y},
	{(double)p3.x,(double)p3.y},
	{(double)p4.x,(double)p4.y} };
	Mat C = Mat(4, 2, CV_64FC1, C_data);
	Mat A = M * C;
	for (double t = 0.0; t <= 1.0; t += 0.001)
	{
		double x = (A.ptr<double>(0)[0] * (t*t*t)) + (A.ptr<double>(1)[0] * (t*t)) + (A.ptr<double>(2)[0] * t) + (A.ptr<double>(3)[0]);
		double y = (A.ptr<double>(0)[1] * (t*t*t)) + (A.ptr<double>(1)[1] * (t*t)) + (A.ptr<double>(2)[1] * t) + (A.ptr<double>(3)[1]);
		circle(canvas, Point(x, y), 1, Scalar(0, 0, 0), 1);
	}
	if (lc == 0) {
		line(canvas, p1, p2, Scalar(0, 0, 255), 1);
		line(canvas, p3, p4, Scalar(0, 0, 255), 1);
	}
	else if(lc ==1)
	{
		line(canvas, p1, p2, Scalar(255, 255, 255), 1);
		line(canvas, p3, p4, Scalar(255, 255, 255), 1);
	}
}
void drawPoints(int red, int green,int blue)
{
	for (int i = 0;i < pl.size();i++)
	{
		circle(canvas, pl[i], 1, Scalar(blue, green, red), 2);
	}
}
void drawSpline()
{
	int sn = pl.size()/4;
	for (int i = 0; i < sn;i++)
	{
		int x = i * 4;
		splines(pl[x + 0], pl[x + 1], pl[x + 2], pl[x + 3],linectrl);
	}
}

void myMouse(int event, int x, int y, int, void*)
{

	if (event == CV_EVENT_LBUTTONDOWN)
	{

			for (int i = 0;i < pl.size();i++)
			{

				if (abs(pl[i].x - x) + abs(pl[i].y - y) <= 10)
				{

					idx = i;
				}

			}

		if (idx < 0) {

			pl.push_back(Point(x, y));
		}
	}
	else if (event == CV_EVENT_LBUTTONUP)
	{
		idx = -1;

	}
	else if (event == CV_EVENT_RBUTTONDOWN)
	{
		pl.pop_back();
	}
	else if (event == CV_EVENT_MOUSEMOVE)
	{
		if (idx > -1)
		{
			pl[idx].x = x;
			pl[idx].y = y;
		}
	}
}

int main()
{
	canvas = Mat(800, 1000, CV_8UC3); //create an image 1000x800



	namedWindow("painting"); //declare the window first if a mouse event needs to be registered
	setMouseCallback("painting", myMouse);
	while (1)
	{
		canvas = Scalar(255, 255, 255);
		drawPoints(r,g,b);
		drawSpline();
		imshow("painting", canvas);
		char c = waitKey(1);
		if (c == 32)
			break;
		else if (c == 'r' ||  c == 'R')
		{
			b = 255;
			g = 255;
			linectrl = 1;
		}
		else if (c == 's' || c == 'S')
		{
			imwrite("SplineImage.jpg", canvas);
		}
	}

	return 0;
}
