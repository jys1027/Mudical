
from os.path import dirname, join
import librosa
import numpy as np

#from pydub import AudioSegment

#import tensorflow as tf
from tensorflow import keras


def noise(data):
    noise_amp = 0.035*np.random.uniform()*np.amax(data)
    data = data + noise_amp*np.random.normal(size=data.shape[0])
    return data

def stretch(data, rate=0.8):
    return librosa.effects.time_stretch(data, rate)

def shift(data):
    shift_range = int(np.random.uniform(low=-5, high = 5)*1000)
    return np.roll(data, shift_range)

def pitch(data, sampling_rate, pitch_factor=0.7):
    return librosa.effects.pitch_shift(data, sampling_rate, pitch_factor)

def extract_features(data, sample_rate):
    # ZCR
    result = np.array([])
    zcr = np.mean(librosa.feature.zero_crossing_rate(y=data).T, axis=0)
    result=np.hstack((result, zcr))

    # Chroma_stft
    stft = np.abs(librosa.stft(data))
    chroma_stft = np.mean(librosa.feature.chroma_stft(S=stft, sr=sample_rate).T, axis=0)
    result = np.hstack((result, chroma_stft))

    # MFCC
    mfcc = np.mean(librosa.feature.mfcc(y=data, sr=sample_rate).T, axis=0)
    result = np.hstack((result, mfcc))

    # Root Mean Square Value
    rms = np.mean(librosa.feature.rms(y=data).T, axis=0)
    result = np.hstack((result, rms))

    # MelSpectogram
    mel = np.mean(librosa.feature.melspectrogram(y=data, sr=sample_rate).T, axis=0)
    result = np.hstack((result, mel))

    return result

def get_features(path):
    # duration and offset are used to take care of the no audio in start and the ending of each audio files as seen above.
    data, sample_rate = librosa.load(path, duration=2.5, offset=0.6)

    #audio_data = AudioSegment.from_file(path)
    #sample_rate = audio_data.frame_rate
    #data = audiosegment_to_librosawav(audio_data)


    # without augmentation
    res1 = extract_features(data, sample_rate)
    result = np.array(res1)

    # data with noise
    noise_data = noise(data)
    res2 = extract_features(noise_data, sample_rate)
    result = np.vstack((result, res2)) # stacking vertically

    # data with stretching and pitching
    new_data = stretch(data)
    data_stretch_pitch = pitch(new_data, sample_rate)
    res3 = extract_features(data_stretch_pitch, sample_rate)
    result = np.vstack((result, res3)) # stacking vertically

    return result

#https://stackoverflow.com/questions/58810035/converting-audio-files-between-pydub-and-librosa
'''
def audiosegment_to_librosawav(audiosegment):
    channel_sounds = audiosegment.split_to_mono()
    samples = [s.get_array_of_samples() for s in channel_sounds]

    fp_arr = np.array(samples).T.astype(np.float32)
    fp_arr /= np.iinfo(samples[0].typecode).max
    fp_arr = fp_arr.reshape(-1)

    return fp_arr
'''


model_name = "voice_model_2.1.0"
voice_model_filename = join(dirname(__file__), model_name)
model = keras.models.load_model(voice_model_filename)


def get_emotion(absolute_filename):

    #audio_file = AudioSegment.from_file(absolute_filename,  format= 'm4a')
    #file_handle = audio_file.export(wav_filename, format='wav')


    data, sample_rate = librosa.load(absolute_filename, duration=2.5, offset=0.6)

    x_val = extract_features(data, sample_rate)

    x_val = np.expand_dims(x_val, axis = 0)

    x_val = np.expand_dims(x_val, axis = -1)

    y_val = model.predict(x_val)

    print(y_val)

    y_val = y_val[0]

    return y_val


    #try:
        #return get_features(absolute_filename)
    #finally:
        #return absolute_filename

    #try:
        #with open(absolute_filename, 'r') as f:
            #return f.readlines()
    #finally:
        #return "file open failed"

