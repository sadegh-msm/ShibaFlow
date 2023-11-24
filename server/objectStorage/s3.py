import boto3
from configs import config
from botocore.exceptions import ClientError


def arvan_uploader(endpoint_url, access_key, secret_key, bucket_name, file, key):
    try:
        s3_resource = boto3.resource(
            's3',
            endpoint_url=endpoint_url,
            aws_access_key_id=access_key,
            aws_secret_access_key=secret_key
        )
    except Exception as exc:
        config.logging.error(exc)
    else:
        try:
            bucket = s3_resource.Bucket(bucket_name)
            object_name = key

            bucket.put_object(
                ACL='private',
                Body=file,
                Key=object_name
            )

        except ClientError as e:
            config.logging.error(e)


def arvan_downloader(endpoint_url, access_key, secret_key, bucket_name, key, type):
    try:
        s3_resource = boto3.resource(
            's3',
            endpoint_url=endpoint_url,
            aws_access_key_id=access_key,
            aws_secret_access_key=secret_key
        )
    except Exception as exc:
        config.logging.error(exc)
    else:
        try:
            bucket = s3_resource.Bucket(bucket_name)
            object_name = key

            if type == 'music':
                bucket.download_file(
                    Key=object_name,
                    Filename='./Flows/' + object_name
                )
            elif type == 'cover':
                bucket.download_file(
                    Key=object_name,
                    Filename='./Cover_flows/' + object_name
                )

        except ClientError as e:
            config.logging.error(e)
