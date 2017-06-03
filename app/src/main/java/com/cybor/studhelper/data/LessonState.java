package com.cybor.studhelper.data;


public class LessonState
{
    public static final byte LESSON = 0, BREAK = 1, FREE_TIME = 2;

    private Lesson _lesson;
    private byte _state;

    public LessonState(Lesson lesson, byte state)
    {
        _lesson = lesson;
        _state = state;
    }

    public Lesson getLesson()
    {
        return _lesson;
    }

    public byte getState()
    {
        return _state;
    }
}
