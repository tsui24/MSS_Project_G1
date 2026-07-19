package com.hotel.room.dto;

import jakarta.validation.constraints.Min;

public class ChecklistStepRequest {
    @Min(0)
    private int stepIndex;
    private boolean checked;

    public int getStepIndex() { return stepIndex; }
    public void setStepIndex(int stepIndex) { this.stepIndex = stepIndex; }
    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }
}
