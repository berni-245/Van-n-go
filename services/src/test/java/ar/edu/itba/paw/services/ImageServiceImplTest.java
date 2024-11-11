package ar.edu.itba.paw.services;

import ar.edu.itba.paw.exceptions.InvalidImageException;
import ar.edu.itba.paw.models.Client;
import ar.edu.itba.paw.persistence.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ImageServiceImplTest {
    @Mock
    private ImageDao imgDao;
    @Mock
    private BookingDao bookingDao;
    @Mock
    private MailService mailService;

    @InjectMocks
    private ImageServiceImpl imageService;

    private static final String EMPTY_FILE_NAME = "";
    private static final String NORMAL_FILE_NAME = "file.png";
    private static final byte[] NORMAL_BYTES = new byte[]{1, 2, 3, 4, 5};
    private static final byte[] EXCESSIVE_BYTES = new byte[10 * 1024 * 1024 + 1];

    private static final Integer NORMAL_BOOKING_ID = 1;

    @Test(expected = InvalidImageException.class)
    public void testInvalidImageUploadPfpEmptyFileName() {
        imageService.uploadPfp(null, NORMAL_BYTES, EMPTY_FILE_NAME);

        fail();
    }

    @Test(expected = InvalidImageException.class)
    public void testInvalidImageUploadPfpNullFileName() {
        imageService.uploadPfp(null, NORMAL_BYTES, null);

        fail();
    }

    @Test(expected = InvalidImageException.class)
    public void testInvalidImageUploadPfpNullBytesArray() {
        imageService.uploadPfp(null, null, NORMAL_FILE_NAME);

        fail();
    }

    @Test(expected = InvalidImageException.class)
    public void testInvalidImageUploadPfpFileTooBig() {
        imageService.uploadPfp(null, EXCESSIVE_BYTES, NORMAL_FILE_NAME);

        fail();
    }

    @Test(expected = InvalidImageException.class)
    public void testInvalidImageUploadPopEmptyFileName() {
        final Client preexistingClient = spy(Client.class);
        imageService.uploadPop(preexistingClient, NORMAL_BYTES, EMPTY_FILE_NAME, NORMAL_BOOKING_ID);

        fail();
    }

    @Test(expected = InvalidImageException.class)
    public void testInvalidImageUploadPopNullFileName() {
        final Client preexistingClient = spy(Client.class);
        imageService.uploadPop(preexistingClient, NORMAL_BYTES, null, NORMAL_BOOKING_ID);

        fail();
    }

    @Test(expected = InvalidImageException.class)
    public void testInvalidImageUploadPopNullBytesArray() {
        final Client preexistingClient = spy(Client.class);
        imageService.uploadPop(preexistingClient, null, NORMAL_FILE_NAME, NORMAL_BOOKING_ID);

        fail();
    }

    @Test(expected = InvalidImageException.class)
    public void testInvalidImageUploadPopFileTooBig() {
        final Client preexistingClient = spy(Client.class);
        imageService.uploadPop(preexistingClient, EXCESSIVE_BYTES, NORMAL_FILE_NAME, NORMAL_BOOKING_ID);

        fail();
    }

}
