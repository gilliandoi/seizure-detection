library("R.matlab")
library("bmp")

#�d�ʐ�
channels <- 0

#���ԑ�
timeTotal <- 0

#�����V���[�Y
series <- 600

#��������(<399)
splitLimit <- 0

#�o�͉摜��
outWidth <- 183

#�o�͉摜��
outHeight <- 183

#���̓t�H���_�p�X�imat�j
inputDir <- '/R/data'

#�o�̓t�H���_�p�X(jpg)
outputDir <- '/R/output'

#�t�H���_�ő��݂���mat�t�@�C���𕪊����ďo��
readMatFolder <- function(){
	setwd(inputDir)

	matfile <- dir()
	cnt <- grep("mat",matfile)
	
	#cat("-----------Split Data----------------------------------------\n")
	for ( i in 1:length(cnt)){
		path = matfile[cnt[i]];	
		print(path)
		readMatFile(path)		
	}

	cat(paste("---------------------------------------------------------------\n"))
	if(splitLimit != 0){
		cat(paste("Exported",splitLimit*length(cnt),"files!\n"))
	}
	setwd('/R')
	#source('/R/test01.R')
}

#matLab�t�@�C���������ďo��
readMatFile <- function(path){
	setwd(inputDir)
	
	x <- readMat(path)
	
	#cat("-----------Orgin Data----------------------------------------\n")
	

	#mat�t�@�C���\���o��
	#print(x[[1]])

	# data �s��
	data <- x[[1]][[1]]
	channels = nrow(data)
	timeTotal = ncol(data)
	#cat(paste("row*col:",channels,"*",timeTotal,"\n\n"));
	
	#�f�[�^����
	fileAccount=timeTotal/series-1

	i <- 0
	#�����������ʂ܂ł̃t�@�C�����o�͂���
	if(splitLimit != 0){
		fileAccount=splitLimit
	}
	while(i<fileAccount){		

		#�e�t�@�C���o��
		cat(paste("File",i+1," OK!\n"))

		#�O���t�o�͗p�s��쐬
		outputData <- list()

		j <- 1
		while(j<=channels){
			#�e�d�ʍ��W�Z�o			
			#���ԑѕ���
			timeFrom <- i*series+1
			timeTo <- (i+1)*series
			#cat(paste("rowIndex:",j,",range:",timeFrom,":",timeTo,"\n"))
			electrode <- data[j,timeFrom :timeTo ]
			#�I���W�i���f�[�^�imat�j
			#str(electrode)
			#�x�N�g���o�͂��邽�߁Ay�l��ύX
			electrode <- electrode*0.14+(channels-j)*32

			#�쐬�����x�N�g���� x �̖����ɒǉ�����
			outputData <- c(outputData,list(electrode))	

			#�O���t�o��
			if (j==1){
				#lim:���W�͈́Alab�F���W���x���Aaxes�F���W����\��
				par(mar=c(0,0,0,0))
				plot(outputData[[1]],type="l",ylim=c(0,500),xlab="",ylab="",axes=FALSE)
			}else{
				#�����摜�ŕҏW
				par(new=T)
				#lim:���W�͈́Alab�F���W���x���Aaxes�F���W����\��
				plot(outputData[[j]],type="l",ylim=c(0,500),xlab="",ylab="",axes=FALSE)
			}			
		
			j <- j + 1
		}


		#�t�@�C���o��
		setwd(outputDir)
		
		#�o�̓t�@�C����
		outputFile <- paste(substr(path, 1, nchar(path) - 4),"_",formatC(i+1,width=3,flag="0"),".bmp",sep="")
		dev.copy(bmp,outputFile,width=outWidth,height=outWidth)
		dev.off()
	
		#���W�o��
		#cat("-----------Output Data-------------\n")
		#str(outputData)
		
		i <- i + 1
	}	
}

#TEST�p�FBMP�t�@�C���Ǎ���
readBMP <- function(){	
	testimg <- read.bmp('/R/output/Dog_1_interictal_segment_0001_001.bmp')
	cat(dim(testimg))
	print(testimg)
}

readMatFolder()