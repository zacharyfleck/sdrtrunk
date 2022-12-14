/*
 * *****************************************************************************
 * Copyright (C) 2014-2022 Dennis Sheirer
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
 * ****************************************************************************
 */

package io.github.dsheirer.dsp.oscillator;

public interface IOscillator
{
    /**
     * Frequency of the tone being generated by the oscillator
     * @return frequency in hertz
     */
    double getFrequency();

    /**
     * Sets the frequency of the tone being generated by the oscillator.
     * @param frequency in hertz
     */
    void setFrequency(double frequency);

    /**
     * Indicates if the current frequency value is non-zero.
     */
    boolean hasFrequency();

    /**
     * Sample rate of the oscillator
     * @return sample rate in hertz
     */
    double getSampleRate();

    /**
     * Sets the sample rate of the oscillator
     * @param sampleRate in hertz
     */
    void setSampleRate(double sampleRate);


    /**
     * Indicates if this oscillator is enabled, meaning that it has a non-zero frequency value.
     * @return true if the frequency value is non-zero
     */
    boolean isEnabled();
}
