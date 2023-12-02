package course.concurrency.exams.refactoring;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.mockito.Mockito.*;

public class MountTableRefresherServiceTests {

    private MountTableRefresherService service;
    private MountTableRefresher refresher;

    private Others.RouterStore routerStore;
    private Others.MountTableManager manager;
    private Others.LoadingCache routerClientsCache;

    @BeforeEach
    public void setUpStreams() {
        service = new MountTableRefresherService();
        service.setCacheUpdateTimeout(1000);
        routerStore = mock(Others.RouterStore.class);
        manager = mock(Others.MountTableManager.class);
        service.setRouterStore(routerStore);
        routerClientsCache = mock(Others.LoadingCache.class);
        service.setRouterClientsCache(routerClientsCache);
        refresher = mock(MountTableRefresher.class);

        // service.serviceInit(); // needed for complex class testing, not for now
    }

    @AfterEach
    public void restoreStreams() {
        // service.serviceStop();
    }

    @Test
    @DisplayName("All tasks are completed successfully")
    public void allDone() {
        // given
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("123", "local6", "789", "local");

        when(refresher.isSuccess()).thenReturn(true);
        List<Others.RouterState> states = addresses.stream()
                .map(Others.RouterState::new).collect(toList());
        when(routerStore.getCachedRecords()).thenReturn(states);
        when(mockedService.getRefresher(anyString())).thenReturn(refresher);

        // when
        mockedService.refresh();

        // then
        verify(mockedService).log("Mount table entries cache refresh successCount=4,failureCount=0");
        verify(routerClientsCache, never()).invalidate(anyString());
    }

    @Test
    @DisplayName("All tasks failed")
    public void noSuccessfulTasks() {
        // given
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("123", "local6", "789", "local");

        when(refresher.isSuccess()).thenReturn(false);
        List<Others.RouterState> states = addresses.stream()
                .map(Others.RouterState::new).collect(toList());
        when(routerStore.getCachedRecords()).thenReturn(states);
        when(mockedService.getRefresher(anyString())).thenReturn(refresher);

        // when
        mockedService.refresh();

        // then
        verify(mockedService).log("Mount table entries cache refresh successCount=0,failureCount=4");
        verify(routerClientsCache, never()).invalidate(anyString());
    }

    @Test
    @DisplayName("Some tasks failed")
    public void halfSuccessedTasks() {
        // given
        MountTableRefresherService mockedService = Mockito.spy(service);
        List<String> addresses = List.of("123", "local6", "789", "local");
        MountTableRefresher successRefresher = spy(refresher);

        when(refresher.isSuccess()).thenReturn(false);
        when(successRefresher.isSuccess()).thenReturn(true);

        List<Others.RouterState> states = addresses.stream()
                .map(Others.RouterState::new).collect(toList());
        when(routerStore.getCachedRecords()).thenReturn(states);
        when(mockedService.getRefresher(addresses.get(0))).thenReturn(refresher);
        when(mockedService.getRefresher(addresses.get(1))).thenReturn(refresher);
        when(mockedService.getRefresher(addresses.get(2))).thenReturn(successRefresher);
        when(mockedService.getRefresher(addresses.get(3))).thenReturn(successRefresher);

        // when
        mockedService.refresh();

        // then
        verify(mockedService).log("Mount table entries cache refresh successCount=2,failureCount=2");
    }

    @Test
    @DisplayName("One task completed with exception")
    public void exceptionInOneTask() {

    }

    @Test
    @DisplayName("One task exceeds timeout")
    public void oneTaskExceedTimeout() {

    }

}
