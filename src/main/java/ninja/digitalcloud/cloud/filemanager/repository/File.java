package ninja.digitalcloud.cloud.filemanager.repository;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.hateoas.RepresentationModel;

import java.util.UUID;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "File", description = "A file descriptor.", requiredProperties = {"name", "content-type"})
@Table(schema = "FILEMANAGER", name = "FILES")
public class File extends RepresentationModel<File> {

    @Id
    @Column(name = "ID", columnDefinition = "uuid default random_uuid()", updatable = false, nullable = false)
    @GeneratedValue(generator = "UUID")
    @JsonProperty(value = "id", index = 1)
    @Schema(description = "The file's unique identifier", example = "a9effbb3-4178-f6e9-08a7-e9520c4620d7")
    private UUID id;

    @Column(name = "NAME")
    @JsonProperty(value = "name", index = 2)
    @Schema(description = "The file's original name", example = "example.csv")
    @Size(max = 50, message = "Invalid file name.")
    private String name;

    @Column(name = "CONTENT_TYPE")
    @JsonProperty(value = "content-type", index = 3)
    @Schema(description = "The file's content-type.", example = "text/csv")
    @Size(max = 50, message = "Invalid content-type.")
    private String contentType;

    @Column(name = "SIZE")
    @JsonProperty(value = "size", index = 4)
    @Schema(description = "The file's size in bytes", example = "0")
    private Long size;

    @JsonIgnore
    @ToString.Exclude
    @Lob
    @Column(name = "DATA")
    private byte[] data;
}
