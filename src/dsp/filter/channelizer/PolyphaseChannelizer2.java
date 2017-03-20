/*******************************************************************************
 * sdrtrunk
 * Copyright (C) 2014-2017 Dennis Sheirer
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 *
 ******************************************************************************/
package dsp.filter.channelizer;

import dsp.filter.FilterFactory;
import dsp.filter.Window;
import dsp.filter.fir.real.RealFIRFilter;
import dsp.mixer.Oscillator;
import org.jtransforms.fft.FloatFFT_1D;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sample.Listener;
import sample.complex.ComplexBuffer;

import java.text.DecimalFormat;

public class PolyphaseChannelizer2 implements Listener<ComplexBuffer>
{
    private final static Logger mLog = LoggerFactory.getLogger(PolyphaseChannelizer2.class);

    private RealFIRFilter[] mInphaseFilters;
    private RealFIRFilter[] mQuadratureFilters;
    private int mFilterPointer = 0;
    private int mChannelCount;
    private int mBlockSize;
    private float[] mFilteredSamples;
    private FloatFFT_1D mFFT;
    private ChannelDistributor mChannelDistributor;

    private DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.0000");

    /**
     * Creates a polyphase filter bank that divides the input frequency band into evenly spaced channels that
     * are oversampled by 2x for output.
     *
     * @param taps of a low-pass filter designed for the inbound sample rate with a cutoff frequency
     * equal to the channel bandwidth (sample rate / filters).  If you need to synthesize (combine two or more
     * filte outputs) a new bandwidth signal from the outputs of this filter, then the filter should be designed
     * as a nyquist filter with -6 dB attenuation at the channel bandwidth cutoff frequency
     *
     * @param channels - number of filters/channels to output.  Since this filter bank oversamples each filter
     * output, this number must be even (divisible by the oversample rate).
     */
    public PolyphaseChannelizer2(float[] taps, int channels)
    {
        assert(channels % 2 == 0);

        mChannelCount = channels;

        initFilters(taps);
    }

    public void filter(float inphase, float quadrature)
    {
        int index = (mChannelCount - mFilterPointer - 1) * 2;

        mFilteredSamples[index] = mInphaseFilters[mFilterPointer].filter(inphase);
        mFilteredSamples[index + 1] = mQuadratureFilters[mFilterPointer].filter(quadrature);

        if(mFilterPointer == 0)
        {
            calculate();
            mFilterPointer = mChannelCount - 1;
        }
        else
        {
            mFilterPointer--;
        }
    }

    @Override
    public void receive(ComplexBuffer complexBuffer)
    {
        float[] samples = complexBuffer.getSamples();

        for(int x = 0; x < samples.length; x += 2)
        {
            filter(samples[x], samples[x + 1]);
        }
    }

    private void calculate()
    {
        float[] samples = new float[mChannelCount * 2];
        System.arraycopy(mFilteredSamples, 0, samples, 0, mFilteredSamples.length);

        //FFT is executed in-place where the output overwrites the input
        mFFT.complexForward(samples);

        dispatch(samples);
    }

    private void dispatch(float[] channels)
    {
        if(mChannelDistributor != null)
        {
            mChannelDistributor.receive(channels);
        }
    }

    public void setChannelDistributor(ChannelDistributor channelDistributor)
    {
        mChannelDistributor = channelDistributor;
    }

    private double magnitude(float real, float imaginary, int fftSize)
    {
        return Math.sqrt(Math.pow(real, 2.0) + Math.pow(imaginary, 2.0));
    }

    /**
     * Distributes the filter taps to each of the polyphase filters
     *
     * @param taps
     */
    private void initFilters(float[] taps)
    {
        int tapCount = (int)Math.ceil((double)taps.length / (double)mChannelCount);

        float[][] coefficients = new float[mChannelCount][tapCount];

        for(int tap = 0; tap < tapCount; tap++)
        {
            for(int filter = 0; filter < mChannelCount; filter++)
            {
                int index = tap * mChannelCount + filter;

                if(index < taps.length)
                {
                    coefficients[filter][tap] = taps[index];
                }
            }
        }

        mInphaseFilters = new RealFIRFilter[mChannelCount];
        mQuadratureFilters = new RealFIRFilter[mChannelCount];

        for(int filter = 0; filter < mChannelCount; filter++)
        {
            mInphaseFilters[filter] = new RealFIRFilter(coefficients[filter], 1.0f);
            mQuadratureFilters[filter] = new RealFIRFilter(coefficients[filter], 1.0f);
        }

        mFilteredSamples = new float[mChannelCount * 2];
        mFFT = new FloatFFT_1D(mChannelCount);
        mFilterPointer = mChannelCount -1;
    }

    public static void main(String[] args)
    {
        mLog.debug("Starting ...");

        int symbolRate = 4800;
        int samplesPerSymbol = 2;
        int channels = 800;
        int channelBandwidth = 12500;
        int sampleRate = channels * channelBandwidth;

        float[] taps = FilterFactory.getLowPass( 10000000, 12500, 5600, Window.WindowType.BLACKMAN_HARRIS_7 );

        int channelCenter = (int)(12500 * 1.5);

        StringBuilder sb = new StringBuilder();
        sb.append("\nPolyphase Channelizer\n");
        sb.append("Sample Rate:" + sampleRate + " Channels:" + channels + " Channel Rate:" + channelBandwidth + "\n");
        sb.append("Tap Count:" + taps.length + "\n");
        sb.append("Channel:" + channelCenter);

        mLog.debug(sb.toString());

        PolyphaseChannelizer2 channelizer = new PolyphaseChannelizer2(taps, 8);

        Oscillator oscillator = new Oscillator(channelCenter, 2 * sampleRate);

        for(int x = 0; x < 2000; x++)
        {
            channelizer.filter(oscillator.getComplex().inphase(), oscillator.getComplex().quadrature());
            oscillator.rotate();
        }

        mLog.debug("Finished!");
    }
}
