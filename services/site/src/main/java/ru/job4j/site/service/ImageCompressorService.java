package ru.job4j.site.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.site.util.MultipartFileImpl;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * ImageCompressorService
 * Класс реализует сжатие изображения для загрузки на сервис.
 *
 * @author Dmitry Stepanov, user Dmitry
 * @since 04.10.2023
 */

@Service
public class ImageCompressorService implements ImageCompress {
    private final int imageSizePixel;

    public ImageCompressorService(@Value("${server.site.imageSizePixel}") String imageSizePixel) {
        this.imageSizePixel = Integer.parseInt(imageSizePixel);
    }

    /**
     * Метод уменьшает размер изображения до необходимого размера.
     * Цель метода уменьшить размер загружаемого файла,
     * при этом уменьшение размера файла происходить за счет сжатия по высоте и ширине изображения.
     *
     * @param file MultipartFile
     * @return MultipartFile
     * @throws IOException exception load file body
     */
    @Override
    public MultipartFile compressImage(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        int[] sizes = getResizeImage(originalImage.getWidth(), originalImage.getHeight());
        int targetWidth = sizes[0];
        int targetHeight = sizes[1];
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage bufferedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        bufferedImage.getGraphics()
                .drawImage(resultingImage, 0, 0, null);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", byteArrayOutputStream);
        bufferedImage.getGraphics().dispose();
        return new MultipartFileImpl(byteArrayOutputStream.toByteArray(), file.getName(),
                file.getOriginalFilename(), file.getContentType());
    }

    /**
     * Метод определяет какую сторону изображения устанавливать заданного размера,
     * и какую сторону вычислять пропорционально.
     * Пример: если ширина изображение больше высоты то ширину устанавливаем требуемому значению,
     * а высоту вычисляем пропорционально. И наоборот при условии когда высота больше ширины.
     *
     * @param originWidth  Width image
     * @param originHeight Height image
     * @return int[] length == 2;
     */
    public int[] getResizeImage(int originWidth, int originHeight) {
        int[] result = new int[2];
        int targetWidth;
        int targetHeight;
        if (originHeight >= originWidth) {
            targetHeight = imageSizePixel;
            targetWidth = targetHeight * originWidth / originHeight;
        } else {
            targetWidth = imageSizePixel;
            targetHeight = targetWidth * originHeight / originWidth;
        }
        result[0] = targetWidth;
        result[1] = targetHeight;
        return result;
    }

}
