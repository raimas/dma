/*******************************************************************************
 * 2008-2012 Public Domain
 * Contributors
 * Marco Lopes (marcolopes@netc.pt)
 * Henry Proudhon and Contributors (henry.proudhon AT insa-lyon.fr)
 * Object Refinery Limited and Contributors.
 * IBM Corporation and others
 *******************************************************************************/
package org.dma.utils.eclipse.swt.graphics;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class SWTImageUtils {

	/**
	 * Creates an SWT image using the current display
	 */
	public static Image createImage(BufferedImage bufferedImage) throws Exception {
		return new Image(Display.getCurrent(), toSWTImage(bufferedImage));
	}


	/**
	 * Creates an SWT image using the current display
	 */
	public static Image createImage(byte[] bytes) throws Exception {
		return new Image(Display.getCurrent(), new ByteArrayInputStream(bytes));
	}


	/**
	 * @return {@link Image} that can be used as placeholder for missing image.
	 */
	public static Image createImage(int size) {
		Image image = new Image(Display.getCurrent(), size, size);
		//
		GC gc = new GC(image);
		gc.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
		gc.fillRectangle(0, 0, size, size);
		gc.dispose();
		//
		return image;
	}


	/**
	 * Returns an {@link Image} encoded by the specified {@link InputStream}.
	 *
	 * @param stream
	 *            the {@link InputStream} encoding the image data
	 * @return the {@link Image} encoded by the specified input stream
	 */
	public static Image createImage(InputStream stream) throws IOException {
		try{
			Display display = Display.getCurrent();
			ImageData data = new ImageData(stream);
			if (data.transparentPixel > 0) {
				return new Image(display, data, data.getTransparencyMask());
			}
			return new Image(display, data);
		} finally {
			stream.close();
		}
	}


	/**
	 * Returns an {@link ImageDescriptor} stored in the file at the specified path.
	 *
	 * @param path
	 *            the path to the image file.
	 * @return the {@link ImageDescriptor} stored in the file at the specified path.
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		try{
			return ImageDescriptor.createFromURL(new File(path).toURI().toURL());
		} catch (MalformedURLException e){
			return null;
		}
	}


	/**
	 * snippet 156: convert between SWT Image and AWT BufferedImage
	 * For a list of all SWT example snippets see
	 * http://www.eclipse.org/swt/snippets/
	 * 2012 - edited to remove compiler warnings - marcolopes@netc.pt
	 */
	public static BufferedImage toAWTImage(ImageData data) {
		BufferedImage bufferedImage = null;
		ColorModel colorModel = null;
		PaletteData palette = data.palette;
		if (palette.isDirect) {
			colorModel = new DirectColorModel(data.depth, palette.redMask, palette.greenMask, palette.blueMask);
			bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width, data.height), false, null);
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					RGB rgb = palette.getRGB(pixel);
					bufferedImage.setRGB(x, y,  rgb.red << 16 | rgb.green << 8 | rgb.blue);
				}
			}
		} else {
			RGB[] rgbs = palette.getRGBs();
			byte[] red = new byte[rgbs.length];
			byte[] green = new byte[rgbs.length];
			byte[] blue = new byte[rgbs.length];
			for (int i = 0; i < rgbs.length; i++) {
				RGB rgb = rgbs[i];
				red[i] = (byte)rgb.red;
				green[i] = (byte)rgb.green;
				blue[i] = (byte)rgb.blue;
			}
			if (data.transparentPixel != -1) {
				colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue, data.transparentPixel);
			} else {
				colorModel = new IndexColorModel(data.depth, rgbs.length, red, green, blue);
			}
			bufferedImage = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(data.width, data.height), false, null);
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int pixel = data.getPixel(x, y);
					pixelArray[0] = pixel;
					raster.setPixel(x, y, pixelArray);
				}
			}
		}
		return bufferedImage;
	}


	/**
	 * snippet 156: convert between SWT Image and AWT BufferedImage
	 * For a list of all SWT example snippets see
	 * http://www.eclipse.org/swt/snippets/
	 * 2012 - edited to remove compiler warnings - marcolopes@netc.pt
	 */
	public static ImageData toSWTImage(BufferedImage bufferedImage) {
		ImageData data = null;
		if (bufferedImage.getColorModel() instanceof DirectColorModel) {
			DirectColorModel colorModel = (DirectColorModel)bufferedImage.getColorModel();
			PaletteData palette = new PaletteData(colorModel.getRedMask(), colorModel.getGreenMask(), colorModel.getBlueMask());
			data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					int rgb = bufferedImage.getRGB(x, y);
					int pixel = palette.getPixel(new RGB((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF));
					data.setPixel(x, y, pixel);
					if (colorModel.hasAlpha()) {
						data.setAlpha(x, y, (rgb >> 24) & 0xFF);
					}
				}
			}
		} else if (bufferedImage.getColorModel() instanceof IndexColorModel) {
			IndexColorModel colorModel = (IndexColorModel)bufferedImage.getColorModel();
			int size = colorModel.getMapSize();
			byte[] reds = new byte[size];
			byte[] greens = new byte[size];
			byte[] blues = new byte[size];
			colorModel.getReds(reds);
			colorModel.getGreens(greens);
			colorModel.getBlues(blues);
			RGB[] rgbs = new RGB[size];
			for (int i = 0; i < rgbs.length; i++) {
				rgbs[i] = new RGB(reds[i] & 0xFF, greens[i] & 0xFF, blues[i] & 0xFF);
			}
			PaletteData palette = new PaletteData(rgbs);
			data = new ImageData(bufferedImage.getWidth(), bufferedImage.getHeight(), colorModel.getPixelSize(), palette);
			data.transparentPixel = colorModel.getTransparentPixel();
			WritableRaster raster = bufferedImage.getRaster();
			int[] pixelArray = new int[1];
			for (int y = 0; y < data.height; y++) {
				for (int x = 0; x < data.width; x++) {
					raster.getPixel(x, y, pixelArray);
					data.setPixel(x, y, pixelArray[0]);
				}
			}
		}
		return data;
	}


}