import numpy as np
from scipy.io import wavfile

sr = 44100
dur = 0.03
t = np.linspace(0, dur, int(sr * dur), endpoint=False)

tone = np.sin(2 * np.pi * 2000 * t)
noise = np.random.uniform(-1, 1, t.size) * 0.3
env = np.exp(-t * 180)
env[-int(sr * 0.008):] *= np.linspace(1, 0, int(sr * 0.008))

click = (tone + noise) * env
click = (click / np.max(np.abs(click)) * 0.8 * 32767).astype(np.int16)
wavfile.write("picker_click.wav", sr, click)