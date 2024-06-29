package ninja.digitalcloud.cloud.filemanager.controller;

import ninja.digitalcloud.cloud.filemanager.exception.BadRequestError;
import ninja.digitalcloud.cloud.filemanager.exception.FileNotFoundError;
import ninja.digitalcloud.cloud.filemanager.repository.File;
import ninja.digitalcloud.cloud.filemanager.repository.FileModelAssembler;
import ninja.digitalcloud.cloud.filemanager.repository.FileRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "/files", produces = {"application/json", "application/hal+json"})
@Tag(name = "Files")
public class FileController {

    private final static Logger logger = LoggerFactory.getLogger(FileController.class);

    private final FileRepository fileRepository;
    private final FileModelAssembler fileModelAssembler;
    private final PagedResourcesAssembler<File> pagedResourcesAssembler;

    @Autowired
    public FileController(FileRepository fileRepository, FileModelAssembler fileModelAssembler,
                          PagedResourcesAssembler<File> pagedResourcesAssembler) {
        this.fileRepository = fileRepository;
        this.fileModelAssembler = fileModelAssembler;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @ApiResponses(value = {
            @ApiResponse(description = "Success: Returns a list of files", responseCode = "200",
                    content = @Content(schema = @Schema(implementation = PagedModel.class, example = """
                    {
                        "_embedded": {
                            "files": [
                                {
                                    "name": "example.csv",
                                    "content-type": "text/csv",
                                    "size": 315,
                                    "_links": {
                                        "self": {
                                            "href": "http://api.example.com/v1/api/files/csv/6f30d7a5-2f2f-444b-965b-9dd5798e2f8c"
                                        }
                                    }
                                }
                            ]
                        },
                        "_links": {
                            "self": {
                                "href": "http://api.example.com/v1/api/files/csv/list?page=0&size=20"
                            }
                        },
                        "page": {
                            "size": 20,
                            "totalElements": 1,
                            "totalPages": 1,
                            "number": 0
                        }
                    }
                    """))),
    })
    @Operation(operationId = "getAllFiles", summary = "List All Files")
    @GetMapping(path = "/csv/list")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PagedModel<EntityModel<File>>> getAllFiles(@ParameterObject Pageable pageable) {
        Page<File> page = fileRepository.findAll(pageable);
        PagedModel<EntityModel<File>> response = pagedResourcesAssembler.toModel(page, fileModelAssembler);
        return ResponseEntity.ok().body(response);
    }

    @ApiResponses(value = {
            @ApiResponse(
                    description = "Returns the newly created file resource",
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(implementation = File.class, example = """
                                    {
                                      "name": "example.csv",
                                      "type": "text/csv",
                                      "size": 1024,
                                      "_links": {
                                        "self": "https://api.example.com/v1/api/files/csv/a9effbb3-4178-f6e9-08a7-e9520c4620d7"
                                      }
                                    }
                                    """))),
            @ApiResponse(
                    description = "Returned when your request is not valid",
                    responseCode = "400",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class, example = """
                                    {
                                        "type": "about:blank",
                                        "title": "Bad Request",
                                        "status": 400,
                                        "detail": "Required part 'File' is not present.",
                                        "instance": "/v1/api/files/csv"
                                    }
                                    """)))
    })
    @Operation(operationId = "uploadFile", summary = "Upload a File")
    @PutMapping(path = "/csv", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EntityModel<File>> uploadFile(@RequestParam(name = "File") MultipartFile file) {
        try {
            if (!file.isEmpty()) {
                File newFile = new File();
                newFile.setName(file.getOriginalFilename());
                newFile.setContentType(file.getContentType());
                newFile.setSize(file.getSize());
                newFile.setData(file.getBytes());
                EntityModel<File> entityModel = fileModelAssembler.toModel(fileRepository.save(newFile));
                return ResponseEntity.ok(entityModel);
            } else {
                throw new BadRequestError("File is empty");
            }
        } catch (IOException exception) {
            logger.error(String.valueOf(exception));
            throw new RuntimeException(exception.getMessage());
        }
    }

    @ApiResponses(value = {
            @ApiResponse(
                    description = "Returns the file.",
                    responseCode = "200"),
            @ApiResponse(
                    description = "Returned when the id is invalid",
                    responseCode = "400",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class, example = """
                                    {
                                        "type": "about:blank",
                                        "title": "Bad Request",
                                        "status": 400,
                                        "detail": "Failed to convert 'id' with value: 'u8c6bd418-a548-3b09-1caf-60ee96b5b533'",
                                        "instance": "/v1/api/files/csv/u8c6bd418-a548-3b09-1caf-60ee96b5b533"
                                    }
                                    """))),
            @ApiResponse(
                    description = "Returned when the file is not found",
                    responseCode = "404",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class, example = """
                                    {
                                        "type": "about:blank",
                                        "title": "Not Found",
                                        "status": 404,
                                        "detail": "File ID: '2f5797df-3c7c-9a28-5ebf-3ef70da7bed4' was not found.",
                                        "instance": "/v1/api/files/csv/2f5797df-3c7c-9a28-5ebf-3ef70da7bed4"
                                    }
                                    """)))
    })
    @Operation(operationId = "getFile", summary = "Read a File")
    @GetMapping(path = "/csv/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> getFile(@PathVariable UUID id) {
        File file = fileRepository.findById(id).orElseThrow(() -> new FileNotFoundError(id.toString()));
        return ResponseEntity.ok(file);
    }

    @ApiResponses(value = {
            @ApiResponse(
                    description = "Successfully deleted.",
                    responseCode = "200",
                    content = @Content(
                            schema = @Schema(example = """
                                    {}
                                    """))),
            @ApiResponse(
                    description = "Returned when the id is invalid",
                    responseCode = "400",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class, example = """
                                    {
                                        "type": "about:blank",
                                        "title": "Bad Request",
                                        "status": 400,
                                        "detail": "Failed to convert 'id' with value: 'u8c6bd418-a548-3b09-1caf-60ee96b5b533'",
                                        "instance": "/v1/api/files/csv/u8c6bd418-a548-3b09-1caf-60ee96b5b533"
                                    }
                                    """))),
            @ApiResponse(
                    description = "Returned when the file is not found",
                    responseCode = "404",
                    content = @Content(
                            schema = @Schema(implementation = ProblemDetail.class, example = """
                                    {
                                        "type": "about:blank",
                                        "title": "Not Found",
                                        "status": 404,
                                        "detail": "File ID: '0b4b873e-424d-cba3-8165-903f47f23183' was not found.",
                                        "instance": "/v1/api/files/csv/0b4b873e-424d-cba3-8165-903f47f23183"
                                    }
                                    """)))
    })
    @Operation(operationId = "deleteFile", summary = "Delete a File")
    @DeleteMapping(path = "/csv/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteFile(@PathVariable UUID id) {
        fileRepository.delete(fileRepository.findById(id).orElseThrow(() -> new FileNotFoundError(id.toString())));
        return ResponseEntity.ok().build();
    }

}
