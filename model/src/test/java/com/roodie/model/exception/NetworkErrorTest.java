package com.roodie.model.exception;

import com.roodie.model.network.NetworkError;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import retrofit.RetrofitError;

import static org.mockito.Mockito.verify;

/**
 * Created by Roodie on 11.07.2016.
 */

/*@RunWith(AndroidJUnit4.class)
@SmallTest*/
public class NetworkErrorTest {

    @Mock
    RetrofitError mockError;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetErrorResponceInteraction() {
        NetworkError.from(mockError);

        verify(mockError).getResponse();
    }
}
