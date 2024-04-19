package com.hiren.boulevard.Listeners;

import com.hiren.boulevard.Models.InstructionResponse;

import java.util.List;

public interface InstructionListener {
    void didFetch(List<InstructionResponse> response, String message);
    void didError(String message);
}
