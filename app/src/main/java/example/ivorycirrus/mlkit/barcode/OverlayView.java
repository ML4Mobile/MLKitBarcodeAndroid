package example.ivorycirrus.mlkit.barcode;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

/** Draw camera overlays of detected information */
public class OverlayView extends View {

    private Rect mRect;
    private String mText;

    public OverlayView(Context c) {
        super(c);
    }

    public void setOverlay(Rect rect, String text){
        mRect = rect;
        mText = text;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(mRect != null) {
            Paint p = new Paint();
            p.setColor(Color.RED);
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(4.5f);
            canvas.drawRect(mRect, p);

            if(mText != null) {
                p.setTextSize(80);
                canvas.drawText(mText, mRect.left, mRect.bottom+90, p);
            }
        }
    }
}
