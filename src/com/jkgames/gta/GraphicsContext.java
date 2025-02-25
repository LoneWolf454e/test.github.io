package com.jkgames.gta;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.opengl.GLES11;


public class GraphicsContext
{
	private Canvas canvas;
	private Matrix matrix = new Matrix();
	private Matrix identityMatrix = new Matrix();
	private Paint paint = new Paint();
	private Camera camera;
	private OBB2D viewRect;
	private boolean hasIdentity = false;
	private Rect dstRect = new Rect();
	private Rect srcRect = new Rect();

	public GraphicsContext()
	{
		paint.setColor(Color.BLACK);
	}
	
	public void additiveBlend()
	{
			GLES11.glEnable(GLES11.GL_BLEND);
			GLES11.glBlendFunc(GLES11.GL_ONE, GLES11.GL_ONE);
	}
	
	public void restoreBlend()
	{
		GLES11.glEnable(GLES11.GL_BLEND);
		GLES11.glBlendFunc(GLES11.GL_ONE, GLES11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public Canvas getCanvas() 
	{
		return canvas;
	}

	public void setCanvas(Canvas canvas) 
	{
		this.canvas = canvas;
		if(!hasIdentity)
		{
			hasIdentity = true;
			setIdentity(canvas.getMatrix());
		}
	}
	
	public void setIdentity(Matrix m)
	{
		identityMatrix.set(m);
	}
	
	public void drawRotatedScaledBitmap(Bitmap b, 
			float centerX, float centerY,
			float width, float height, float angle)
	{
		float scaleX = width / b.getWidth();
		float scaleY = height / b.getHeight();
		centerX -= (b.getWidth() * scaleX) / 2.0f;
		centerY -= (b.getHeight() * scaleY) / 2.0f;
		matrix.reset();
		matrix.setTranslate(centerX, centerY);
		matrix.postRotate(angle * (180.0f / (float)(Math.PI)),
				centerX + (b.getWidth() * scaleX) / 2.0f,
				centerY + (b.getHeight() * scaleY) / 2.0f); 
		matrix.preScale(scaleX,scaleY);
		canvas.drawBitmap(b, matrix, null);
	}
	
	public void drawRotatedScaledBitmap(Bitmap b, 
			float centerX, float centerY, float width, float height,
			float sourceX, float sourceY, 
			float sourceWidth, float sourceHeight, float angle)
	{
		float cx = centerX;
		float cy = centerY;

		dstRect.left = (int)centerX - (int)(width / 2);
		dstRect.top = (int)centerY - (int)(height / 2);
		dstRect.right = dstRect.left +  (int)width;
		dstRect.bottom = dstRect.top +  (int)height;
		
		srcRect.left = (int)sourceX;
		srcRect.top = (int)sourceY;
		srcRect.right = srcRect.left +  (int)sourceWidth;
		srcRect.bottom = srcRect.top +  (int)sourceHeight;
	
		canvas.rotate(angle * (180.0f / (float)(Math.PI)),cx,cy);
		canvas.drawBitmap(b, srcRect, dstRect, null);
		canvas.rotate(-angle * (180.0f / (float)(Math.PI)),cx,cy);
	}
	
	public void drawRotatedBitmap(Bitmap b, 
			float centerX, float centerY, float angle)
	{
		drawRotatedScaledBitmap(b, centerX, centerY,
				b.getWidth(), b.getHeight(), angle);
	}
	
	public void drawBitmap(Bitmap b, 
			float centerX, float centerY)
	{
		canvas.drawBitmap(b, centerX - 
				(b.getWidth() / 2), centerY - (b.getHeight() / 2), null);
	}
	
	public void drawBitmap(Bitmap b, 
			float centerX, float centerY, Paint p)
	{
		canvas.drawBitmap(b, centerX -
				(b.getWidth() / 2), centerY - (b.getHeight() / 2), p);
	}
	
	
	public void setBackgroundColor(int c)
	{
		paint.setColor(c);
	}
	
	public void clear()
	{
		getCanvas().drawRect(0, 0, getCanvas().getWidth(), getCanvas().getHeight(), paint);
	}

	public Camera getCamera() 
	{
		return camera;
	}

	public void setCamera(Camera camera) 
	{
		this.camera = camera;
	}
	
	public OBB2D getViewRect()
	{
		return viewRect;
	}
	
	public void identityTransform()
	{
		getCanvas().setMatrix(identityMatrix);
	}
	
	public void generateViewRect()
	{
		viewRect = getCamera().getCamRect(getCanvas().getWidth(), getCanvas().getHeight());
	}
	
	
}
