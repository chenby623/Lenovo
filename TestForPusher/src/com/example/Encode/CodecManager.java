/**
 * Android Ӳ����롣
 */
package com.example.Encode;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import android.annotation.SuppressLint;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.util.Log;

@SuppressLint("InlinedApi")
public class CodecManager {

	public final static String TAG = "CodecManager";

	public static final int[] SUPPORTED_COLOR_FORMATS = {
		MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar,
		MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedSemiPlanar,
		MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Planar,
		MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420PackedPlanar,
		MediaCodecInfo.CodecCapabilities.COLOR_TI_FormatYUV420PackedSemiPlanar
	};		

	private static Codec[] sEncoders = null;
	
	private static Codec[] sDecoders = null;

	static class Codec {
		public Codec(String name, Integer[] formats) {
			this.name = name;
			this.formats = formats;
		}
		public String name;
		public Integer[] formats;
	}

	/**
	 * Lists all encoders that claim to support a color format that we know how to use.
	 * @return A list of those encoders
	 */
	@SuppressLint("NewApi")
	public synchronized static Codec[] findEncodersForMimeType(String mimeType) {
		if (sEncoders != null) return sEncoders;

		ArrayList<Codec> encoders = new ArrayList<Codec>();

		// We loop through the encoders, apparently this can take up to a sec (testes on a GS3)
		for(int j = MediaCodecList.getCodecCount() - 1; j >= 0; j--){
			MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(j);
			if (!codecInfo.isEncoder()) continue;

			String[] types = codecInfo.getSupportedTypes();
			for (int i = 0; i < types.length; i++) {
				if (types[i].equalsIgnoreCase(mimeType)) {
					try {
						MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
						Set<Integer> formats = new HashSet<Integer>();

						// And through the color formats supported
						for (int k = 0; k < capabilities.colorFormats.length; k++) {
							int format = capabilities.colorFormats[k];

							for (int l=0;l<SUPPORTED_COLOR_FORMATS.length;l++) {
								if (format == SUPPORTED_COLOR_FORMATS[l]) {
									formats.add(format);
									break;
								}
							}
						}
						
						Codec codec = new Codec(codecInfo.getName(), (Integer[]) formats.toArray(new Integer[formats.size()]));
						encoders.add(codec);
					} catch (Exception e) {
						Log.wtf(TAG,e);
					}
				}
			}
		}

		sEncoders = (Codec[]) encoders.toArray(new Codec[encoders.size()]);
		if (sEncoders.length == 0) {
			sEncoders = new Codec[]{new Codec(null, new Integer[]{0})};
		}
		return sEncoders;

	}


	@SuppressLint("NewApi")
	public synchronized static Codec[] findDecodersForMimeType(String mimeType) {
		if (sDecoders != null) return sDecoders;
		ArrayList<Codec> decoders = new ArrayList<Codec>();

		// We loop through the decoders, apparently this can take up to a sec (testes on a GS3)
		for(int j = MediaCodecList.getCodecCount() - 1; j >= 0; j--){
			MediaCodecInfo codecInfo = MediaCodecList.getCodecInfoAt(j);
			if (codecInfo.isEncoder()) continue;

			String[] types = codecInfo.getSupportedTypes();
			for (int i = 0; i < types.length; i++) {
				if (types[i].equalsIgnoreCase(mimeType)) {
					try {
						MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(mimeType);
						Set<Integer> formats = new HashSet<Integer>();

						// And through the color formats supported
						for (int k = 0; k < capabilities.colorFormats.length; k++) {
							int format = capabilities.colorFormats[k];

							for (int l=0;l<SUPPORTED_COLOR_FORMATS.length;l++) {
								if (format == SUPPORTED_COLOR_FORMATS[l]) {
									formats.add(format);
									break;
								}
							}
						}

						Codec codec = new Codec(codecInfo.getName(), (Integer[]) formats.toArray(new Integer[formats.size()]));
						decoders.add(codec);
					} catch (Exception e) {
						Log.wtf(TAG,e);
					}
				}
			}
		}

		sDecoders = (Codec[]) decoders.toArray(new Codec[decoders.size()]);

		// We will use the decoder from google first, it seems to work properly on many phones
		for (int i=0;i<sDecoders.length;i++) {
			if (sDecoders[i].name.equalsIgnoreCase("omx.google.h264.decoder")) {
				Codec codec = sDecoders[0];
				sDecoders[0] = sDecoders[i];
				sDecoders[i] = codec;
			} 
		}

		return sDecoders;
	}

}

