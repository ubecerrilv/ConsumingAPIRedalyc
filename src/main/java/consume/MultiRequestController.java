package consume;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@RestController
public class MultiRequestController {

    private final RestTemplate restTemplate;

    public MultiRequestController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/")
    public void fetchData() throws ExecutionException, InterruptedException {
        List<CompletableFuture<Void>> futures = IntStream.rangeClosed(1, 5000).mapToObj(page -> 
            CompletableFuture.runAsync(() -> {
            	String contenido = fetchPageData("date", "2020", page);
            	String nombre = String.format("arch_%d.json", page);
            	saveJsonToFile(contenido, nombre);
            })
        ).collect(Collectors.toList());
       
        //CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        
    }//FIN "/"

    private String fetchPageData(String filtro, String valFilt, int page) {
        String url = String.format("http://api.redalyc.org/search/%s(%s),page(%d),sizePage(1)/output(json)/download(no)/token(OTcvZFNTMFQ2ODgxWWF5VUlYNW5XQT09)", filtro, valFilt,page);
        return restTemplate.getForObject(url, String.class);
    }
    private void saveJsonToFile(String jsonData, String fileName) {
        File file = new File("/home/ubecerril/Descargas/Pruebas_API/" + fileName);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(jsonData);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo " + fileName, e);
        }
    }
    
}
