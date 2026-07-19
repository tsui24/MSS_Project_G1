package com.hotel.room.service;

import com.hotel.room.dto.ChecklistStepRequest;
import com.hotel.room.entity.HousekeepingTask;
import com.hotel.room.entity.Room;
import com.hotel.room.entity.RoomStatus;
import com.hotel.room.repository.HousekeepingTaskRepository;
import com.hotel.room.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HousekeepingTaskServiceTest {
    private HousekeepingTaskRepository tasks;
    private RoomRepository rooms;
    private HousekeepingTaskService service;
    private HousekeepingTask task;

    @BeforeEach
    void setUp() {
        tasks = mock(HousekeepingTaskRepository.class);
        rooms = mock(RoomRepository.class);
        service = new HousekeepingTaskService(tasks, rooms);
        Room room = new Room();
        room.setRoomNumber("101");
        room.setStatus(RoomStatus.DIRTY);
        task = new HousekeepingTask();
        task.setRoom(room);
        task.setStaffId(4L);
        task.setTaskType("CLEANING");
        when(tasks.findById(1L)).thenReturn(Optional.of(task));
        when(tasks.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void startTaskRecordsTimeAndMovesRoomToCleaning() {
        service.updateStatus(1L, "IN_PROGRESS");

        assertEquals("IN_PROGRESS", task.getStatus());
        assertNotNull(task.getStartedAt());
        assertEquals(RoomStatus.CLEANING, task.getRoom().getStatus());
    }

    @Test
    void checklistMustBeCompletedInOrder() {
        task.setStatus("IN_PROGRESS");
        ChecklistStepRequest secondStep = checklist(1, true);

        assertThrows(IllegalArgumentException.class, () -> service.updateChecklist(1L, secondStep));
        assertEquals(0, task.getCompletedSteps());

        service.updateChecklist(1L, checklist(0, true));
        service.updateChecklist(1L, secondStep);
        assertEquals(2, task.getCompletedSteps());
    }

    @Test
    void cannotCompleteUntilAllChecklistStepsArePersisted() {
        task.setStatus("IN_PROGRESS");
        task.getRoom().setStatus(RoomStatus.CLEANING);

        assertThrows(IllegalArgumentException.class, () -> service.updateStatus(1L, "COMPLETED"));
        assertEquals("IN_PROGRESS", task.getStatus());

        task.setCompletedSteps(HousekeepingChecklist.steps(task.getTaskType()).size());
        service.updateStatus(1L, "COMPLETED");
        assertEquals("COMPLETED", task.getStatus());
        assertNotNull(task.getCompletedAt());
        assertEquals(RoomStatus.AVAILABLE, task.getRoom().getStatus());
    }

    private ChecklistStepRequest checklist(int index, boolean checked) {
        ChecklistStepRequest request = new ChecklistStepRequest();
        request.setStepIndex(index);
        request.setChecked(checked);
        return request;
    }
}
